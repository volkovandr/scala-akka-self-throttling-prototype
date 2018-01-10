package volkovandr.sample.AkkaOverloadSimulator

import com.typesafe.config.ConfigFactory
import io.prometheus.client.exporter.HTTPServer

object Metrics {

  private var prometheusHttpServer: HTTPServer = _

  def initMetrics(): Unit = {
    val conf = ConfigFactory.load()
    val metricsPort = conf.getInt("metrics.port")
    prometheusHttpServer = new HTTPServer(metricsPort)
  }

  def shutdownMetrics(): Unit = {
    if(prometheusHttpServer != null)
      prometheusHttpServer.stop()
  }

}
