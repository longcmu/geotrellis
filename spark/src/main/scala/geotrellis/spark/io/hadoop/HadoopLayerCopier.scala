package geotrellis.spark.io.hadoop

import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.spark.io.avro._
import geotrellis.spark.io.index.KeyIndex
import geotrellis.spark.io.json._


import org.apache.avro.Schema
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

import spray.json.JsonFormat
import spray.json.DefaultJsonProtocol._

import scala.reflect.ClassTag

class HadoopLayerCopier(
   rootPath: Path, val attributeStore: AttributeStore[JsonFormat])
  (implicit sc: SparkContext) extends LayerCopier[LayerId] {

  def copy[
    K: Boundable: AvroRecordCodec: JsonFormat: ClassTag,
    V: AvroRecordCodec: ClassTag,
    M: JsonFormat
  ](from: LayerId, to: LayerId): Unit = {
    if (!attributeStore.layerExists(from)) throw new LayerNotFoundError(from)
    if (attributeStore.layerExists(to)) throw new LayerExistsError(to)

    val (header, metadata, keyBounds, keyIndex, writerSchema) = try {
      attributeStore.readLayerAttributes[HadoopLayerHeader, M, KeyBounds[K], KeyIndex[K], Schema](from)
    } catch {
      case e: AttributeNotFoundError => throw new LayerReadError(from).initCause(e)
    }
    val newPath = new Path(rootPath,  s"${to.name}/${to.zoom}")
    HdfsUtils.copyPath(header.path, newPath, sc.hadoopConfiguration)
    attributeStore.writeLayerAttributes(
      to, header.copy(path = newPath), metadata, keyBounds, keyIndex, writerSchema
    )
  }
}

object HadoopLayerCopier {
  def apply(
    rootPath: Path,
    attributeStore: AttributeStore[JsonFormat]
  )(implicit sc: SparkContext): HadoopLayerCopier =
    new HadoopLayerCopier(rootPath, attributeStore)

  def apply(rootPath: Path)(implicit sc: SparkContext): HadoopLayerCopier =
    apply(rootPath, HadoopAttributeStore(rootPath, sc.hadoopConfiguration))

  def apply(attributeStore: HadoopAttributeStore)(implicit sc: SparkContext): HadoopLayerCopier =
    apply(attributeStore.rootPath, attributeStore)
}
