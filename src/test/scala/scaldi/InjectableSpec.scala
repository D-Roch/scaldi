package scaldi

import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers
import java.text.DateFormat

class InjectableSpec extends WordSpec with ShouldMatchers {

  "Injectable" should {

    "be useable within classes that are instantiated within module and have implicit injector" in {
      val module = new TcpModule :: DynamicModule({ m =>
        m.binding identifiedBy 'tcpHost to "test"
        m.binding identifiedBy 'welcome to "Hello user!"
      })

      val binding = module.getBinding(List('tcpServer))
      binding should be ('defined)

      val server = binding.get.get.get.asInstanceOf[TcpServer]
      server.port should be === 1234
      server.host should be === "test"
      server.getConnection.welcomeMessage should be === "Hello user!"
    }

    "treat binding that return None as non-defined and use default of throw exception if no default provided" in {
      val module = new TcpModule :: DynamicModule(_.bind [String] identifiedBy 'welcome to None) :: DynamicModule({ m =>
        m.binding identifiedBy 'tcpHost to "test"
        m.binding identifiedBy 'welcome to "Hello user!"
      })

      val binding = module.getBinding(List('tcpServer))
      binding should be ('defined)

      val server = binding.get.get.get.asInstanceOf[TcpServer]
      server.getConnection.welcomeMessage should be === "Hi"
    }

    import scaldi.Injectable._
    val defaultDb = PostgresqlDatabase("default_db")

    "inject by type" in {
      inject [Database] should be === MysqlDatabase("my_app")
      inject [Database] (classOf[ConnectionProvider]) should be === MysqlDatabase("my_app")
    }

    "inject using identifiers" in {
      val results = List (
        inject [Database] ('database and 'local),
        inject [Database] (identified by 'local and 'database),
        inject [Database] (by default defaultDb and identified by 'database and 'local),
        inject [Database] (by default new MysqlDatabase("my_app") and 'database and 'local),
        inject [Database] ('database, "local"),
        injectWithDefault [Database] ('database, "local")(defaultDb)
      )

      results should have size (6)
      results.distinct should (contain(MysqlDatabase("my_app"): Database) and have size (1))
    }

    "inject default if binding not fould" in {
      val results = List [Database] (
        inject [Database] (identified by 'remote and by default new PostgresqlDatabase("default_db")),
        inject [Database] (identified by 'remote is by default defaultDb),
        inject [Database] ('remote is by default defaultDb),
        inject [Database] ('remote which by default defaultDb),
        inject [Database] ('remote that by default defaultDb),
        inject [Database] (by default defaultDb and identified by 'remote),
        inject (by default defaultDb),
        inject (by default defaultDb and 'local),
        inject (by default new PostgresqlDatabase("default_db")),
        injectWithDefault [Database] ('database, "remote")(defaultDb),
        injectWithDefault ('database, "local")(defaultDb),
        injectWithDefault (defaultDb)
      )

      results should have size (12)
      results.distinct should (contain(defaultDb: Database) and have size (1))
    }

    "throw exception if no default provided and bonding not fould" in {
      evaluating(inject [DateFormat]) should produce [InjectException]
    }

    "also be available in module, but use resulting (compised) injector" in {
      val server = inject [Server] ('real and 'http)
      server should be  === HttpServer("marketing.org", 8081)
    }

    /**
     * Generics would be erased at runtime, so framework is not able to
     * find correct Function2 instance
     */
    "ignore generics and return wrong bindings" in {
      val adderWrong = inject [(Int, Int) => Int]
      evaluating(adderWrong(2, 3)) should produce[ClassCastException]

      val adderRight = inject [(Int, Int) => Int] ('intAdder)
      adderRight(2, 3) should be === 5
    }

    "inject all using type parameter" in {
      injectAllOfType [String] ('host) should
          (contain("www.google.com") and contain("www.yahoo.com") and contain("www.github.com") and have size (3))

      injectAllOfType [HttpServer] should
          (contain(HttpServer("localhost", 80)) and contain(HttpServer("test", 8080)) and have size (2))
    }

    "inject all using without type parameter" in {
      injectAll('host).asInstanceOf[List[String]] should
          (contain("www.google.com") and contain("www.yahoo.com") and contain("www.github.com") and have size (3))

      injectAll('server).asInstanceOf[List[HttpServer]] should
          (contain(HttpServer("localhost", 80)) and contain(HttpServer("test", 8080)) and have size (2))
    }
  }

  implicit lazy val injector: Injector = mainModule :: marketingModule

  val marketingModule = new Module {
    bind [String] identifiedBy 'httpHost to "marketing.org"
  }

  val mainModule = new Module {
    binding identifiedBy 'host and 'google to "www.google.com"
    binding identifiedBy 'host and 'yahoo to "www.yahoo.com"
    binding identifiedBy 'host and 'github to "www.github.com"

    binding identifiedBy 'server to HttpServer("localhost", 80)
    binding identifiedBy 'server to HttpServer("test", 8080)

    binding identifiedBy 'intAdder to ((a: Int, b: Int) => a + b)
    binding identifiedBy 'stringAdder to ((s1: String, s2: String) => s1 + ", " + s2)

    bind [Int] identifiedBy 'httpPort to 8081

    bind [Server] identifiedBy 'real and 'http to HttpServer(inject [String] ('httpHost), inject [Int] ('httpPort))

    binding identifiedBy 'database and "local" to MysqlDatabase("my_app")
  }
}