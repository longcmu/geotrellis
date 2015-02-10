package geotrellis.vector.io.shape.reader

import geotrellis.vector._

import org.scalatest._

/**
  * Tests reading .shp files.
  */
class ShapePointFileReaderSpec extends FunSpec with Matchers {

  def read(path: String) = ShapePointFileReader(path)

  describe("should read shape point files correctly") {

    /*
     * Since this file is rather big (160 polygons), I'm comparing the values
     * with the GeoTools reader's hashcodes instead.
     */
    it("should read demographics.shp correctly") {
      val correctLineSets = Array(
        Set(-1500308061), Set(394500365), Set(-1591898469), Set(-636129849),
        Set(2099665837), Set(1231487165), Set(112309256), Set(751940365),
        Set(1788887353), Set(-818812548), Set(763146183), Set(1447798275),
        Set(-1820753902), Set(1467869601), Set(-1626943280), Set(-131580369),
        Set(1763306247), Set(-685424389), Set(593602561), Set(-1297024179),
        Set(127364772), Set(-2088394373), Set(-1497638470), Set(-2019213835),
        Set(207278681), Set(-1553433517), Set(91591460), Set(135932894),
        Set(2046339012), Set(-13703143), Set(1686060625), Set(-1493775709),
        Set(513350380), Set(-536944549), Set(-945928523, 1193428387),
        Set(-398313850), Set(1507245305), Set(-417299070), Set(1236947366),
        Set(1199381030), Set(1168779386), Set(2003466961), Set(-1087923756),
        Set(-1508967665), Set(-474531864), Set(-175758515), Set(144997813),
        Set(-747363264), Set(-403143120), Set(2105613397), Set(-1685925251),
        Set(660853426), Set(-986379910), Set(-962941293), Set(-1900179320),
        Set(-2022136464), Set(-935923504), Set(-1945857047), Set(1671027464),
        Set(681649179, 1760574422), Set(-700012422), Set(-1235892644),
        Set(-2081755649), Set(1583688547), Set(1984475039), Set(-626336834),
        Set(1209941187), Set(647660047), Set(1034573690), Set(1522729125),
        Set(-272326885), Set(-1506984948), Set(600294865), Set(-1709397131),
        Set(1494342209), Set(950666474), Set(-1912558302), Set(-1928895966),
        Set(-157730769), Set(105077527), Set(-1623010412), Set(1134057993),
        Set(1630258597), Set(-1900815019), Set(-679913187, -940426072),
        Set(1818086056), Set(-995495057), Set(-1087888989), Set(-1667624299),
        Set(982580601), Set(1133377346), Set(1965731728), Set(-1554873840),
        Set(-304464848), Set(1563960613), Set(-634698627), Set(-332296619),
        Set(1222640800), Set(1249481907), Set(1946657137),
        Set(312114006, -1263413160), Set(110844419), Set(-1213820678),
        Set(-1216244490), Set(1779567067), Set(432951756), Set(-1490567945),
        Set(1148600312), Set(1735758194), Set(-1700663549), Set(-511811648),
        Set(-2142278248), Set(-471236689), Set(-693202443), Set(102243779),
        Set(-328366637), Set(-1042343383), Set(455844285, -598184550),
        Set(-426299372, 101176247), Set(-2030841998), Set(1763874556),
        Set(-31254079), Set(1213607719), Set(584829751), Set(1121472236),
        Set(-598192697), Set(-2094697044), Set(2022334282), Set(667222493),
        Set(-154269733), Set(-972606532), Set(948438301), Set(672976367),
        Set(-2036334458), Set(1210863521), Set(-1474938793), Set(763540284),
        Set(1899740858), Set(406499728), Set(667671441), Set(1468767949),
        Set(37440961), Set(-1114887818), Set(-1203309722), Set(-419669350),
        Set(72237257), Set(-1319270443), Set(37556672), Set(-2050962132),
        Set(518465054), Set(136650072), Set(-672958999, -2014944721, -395621507),
        Set(1598638566), Set(-1019959301), Set(-417655962), Set(-711595506),
        Set(1569989940), Set(-1673100106), Set(-409568899), Set(-1166088463)
      )


      val path = "raster-test/data/shapefiles/demographics/demographics.shp"
      val shapePointFile = ShapePointFileReader(path)

      shapePointFile.size should be (160)
      for (i <- 0 until shapePointFile.size)
        shapePointFile(i) match {
          case mp: MultiPolygon =>
            withClue(s"failed on index: $i with multipolygon $mp: ") {
              val lines = mp.polygons.map(p => p.holes :+ p.exterior).flatten
              lines.map(_.hashCode).toSet should be (correctLineSets(i))
            }
          case _ => fail
        }
    }

    /*
     * Since this file is rather big (255 polygons), I'm comparing the values
     * with the GeoTools reader's hashcodes instead.
     */
    it("should read countries.shp correctly") {
      val correctLineSetsHashCodes = Array(
        1063763546, 283051003, -243911973, 1704477527, -1177353649, -565103225,
        -129345143, 613987289, 461019419, -254739055, 1533049527, 1660136994,
        -1498822895, 1014219046, -1487290687, -1610372837, 823857700, 1198252951,
        -1934279313, -1758420656, -1715280191, -1296003985, -359975872, 1725286681,
        -930015058, 1004582895, -401485911, -891750781, 1437157212, 1585982013,
        873476298, -2129083150, 1583194226, -1459370965, 55723987, -647338997,
        -1585273588, 1552038907, 188202862, 1302899560, 1382746419, 925665089,
        1116932733, 1889832650, 160699822, 1890819679, 1703231947, 1262947149,
        900992252, 2106463931, 858806493, -1580756774, 86986943, 2068434251,
        444035349, 359052041, -626550201, 1587359655, -619761936, -673892371,
        878616447, -939187066, -1376836600, -883400011, 2039074738, 978489583,
        -1471381712, 1118920531, 868330896, -1833918853, 1976682091, -1658184105,
        1319302077, 1720586213, -334723393, 1093030526, 661646849, 626256924,
        2068829544, -1656113720, 518586002, -1926072950, -39707194, -822733554,
        259724304, 1582507201, -1256726981, 318106287, -764992405, -1170502906,
        411857303, -585760686, 1576363507, -610008099, 766564135, 912269753,
        55976879, -1888839619, 1860620927, -1195300892, 1476641526, -1127723469,
        -848544967, 1262407727, 1816922630, 1432088819, -1967437372, -112030267,
        -677469169, -1321826914, -1924934515, -770833511, 1999111178, 291584559,
        1218309374, -433997775, -600742176, -1562094205, 2096185852, 218317387,
        181617748, -1870232138, -942666819, -768801652, -1095265813, -678060522,
        -342736197, -1143060739, 1676740214, 620868870, 2041695327, -672682345,
        -676652730, 1946685251, 1258361336, -183117653, -2052544075, 155848238,
        -730741756, 1755725147, 531283861, -110837207, 341989909, 1502769589,
        883527236, 107214880, 2135800775, -1771475792, -1847372226, -277026246,
        1209955310, -1313130502, 1090368039, 328987655, 114157410, 1288643908,
        1989859974, 331218604, 2011231361, 235463269, 1891760131, -935864512,
        1136614612, -1082702358, 950504995, -744934019, 137050999, 1789258727,
        -582827091, 1675191068, -577253400, 149100198, -1598027484, 525395201,
        -1814292649, -112531356, 1950054754, -302756461, 1975744455, 2126029680,
        -1008159751, 689082590, -177728357, 1285815755, -293723954, 274980808,
        -29573151, 1423276574, -879887535, -280922498, -947853026, -1534741891,
        336695643, 1236691994, -1291671169, 119626174, -114289524, 1383404495,
        -1636996525, 1270210622, -1203605482, -617168662, -1717372956, -2075197077,
        1507995805, -1481397226, 388597586, -1876946922, -129152534, -1497417425,
        -2141752705, -87779050, 1920228621, 368756654, 1095848877, 74825791,
        -1116592416, -1176157815, -1017714518, -1576634918, -179230134, 256214072,
        1143836324, -184039507, 2082377274, 1931358462, -1110889860, 631506907,
        514655876, -583866514, -309849689, 880959237, -1879306252, -1786283016,
        1449110087, 305490542, 393094955, 784157876, -1115515956, 1513830491,
        -1735846670, 396069569, -380328803, 1036557997, 1292803602, -1577759130,
        1941775916, -1859187880, 919440271, -153342569, 1323774078, -935939600,
        1907849776, -1964188491, -929905405
      )

      val shapePointFile = read("raster-test/data/shapefiles/countries/countries.shp")

      shapePointFile.size should be (255)
      for (i <- 0 until shapePointFile.size)
        shapePointFile(i) match {
          case mp: MultiPolygon =>
            withClue(s"failed on index: $i with multipolygon $mp: ") {
              val hashCode = mp.polygons.map(p => p.holes :+ p.exterior)
                .flatten.map(_.hashCode).toSet.hashCode

              hashCode should be(correctLineSetsHashCodes(i))
            }
          case _ => fail
        }
    }
  }
}