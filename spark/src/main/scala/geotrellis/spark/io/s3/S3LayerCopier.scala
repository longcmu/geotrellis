package geotrellis.spark.io.s3

import geotrellis.spark.io.avro.AvroRecordCodec
import geotrellis.spark.io.index.KeyIndex
import geotrellis.spark.{Boundable, KeyBounds, LayerId}
import geotrellis.spark.io._
import geotrellis.spark.io.json._

import org.apache.spark.rdd.RDD
import org.apache.avro.Schema
import spray.json.JsonFormat
import com.amazonaws.services.s3.model.ObjectListing

import scala.annotation.tailrec
import scala.collection.JavaConversions._
import scala.reflect.ClassTag

class S3LayerCopier(
  val attributeStore: AttributeStore[JsonFormat],
  destBucket: String,
  destKeyPrefix: String
) extends LayerCopier[LayerId] {

  def getS3Client: () => S3Client = () => S3Client.default

  @tailrec
  final def copyListing(s3Client: S3Client, bucket: String, listing: ObjectListing, from: LayerId, to: LayerId): Unit = {
    listing.getObjectSummaries.foreach { os =>
      val key = os.getKey
      s3Client.copyObject(bucket, key, destBucket, key.replace(s"${from.name}/${from.zoom}", s"${to.name}/${to.zoom}"))
    }
    if (listing.isTruncated) copyListing(s3Client, bucket, s3Client.listNextBatchOfObjects(listing), from, to)
  }

  def copy[
    K: Boundable: AvroRecordCodec: JsonFormat: ClassTag,
    V: AvroRecordCodec: ClassTag,
    M: JsonFormat
  ](from: LayerId, to: LayerId): Unit = {
    if (!attributeStore.layerExists(from)) throw new LayerNotFoundError(from)
    if (attributeStore.layerExists(to)) throw new LayerExistsError(to)

    val (header, metadata, keyBounds, keyIndex, schema) = try {
      attributeStore.readLayerAttributes[S3LayerHeader, M, KeyBounds[K], KeyIndex[K], Schema](from)
    } catch {
      case e: AttributeNotFoundError => throw new LayerReadError(from).initCause(e)
    }

    val bucket = header.bucket
    val prefix = header.key
    val s3Client = getS3Client()

    copyListing(s3Client, bucket, s3Client.listObjects(bucket, prefix), from, to)
    attributeStore.writeLayerAttributes(
      to, header.copy(
        bucket = destBucket,
        key    = makePath(destKeyPrefix, s"${to.name}/${to.zoom}")
      ), metadata, keyBounds, keyIndex, schema
    )
  }
}

object S3LayerCopier {
  def apply(attributeStore: AttributeStore[JsonFormat], destBucket: String, destKeyPrefix: String): S3LayerCopier =
    new S3LayerCopier(attributeStore, destBucket, destKeyPrefix)

  def apply(bucket: String, keyPrefix: String, destBucket: String, destKeyPrefix: String): S3LayerCopier =
    apply(S3AttributeStore(bucket, keyPrefix), destBucket, destKeyPrefix)

  def apply(bucket: String, keyPrefix: String): S3LayerCopier =
    apply(S3AttributeStore(bucket, keyPrefix), bucket, keyPrefix)

  def apply(attributeStore: S3AttributeStore): S3LayerCopier =
    apply(attributeStore, attributeStore.bucket, attributeStore.prefix)
}
