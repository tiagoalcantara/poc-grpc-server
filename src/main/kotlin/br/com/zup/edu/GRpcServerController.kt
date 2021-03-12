package br.com.zup.edu

import io.micronaut.grpc.server.GrpcEmbeddedServer
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import javax.inject.Inject

@Controller
class GRpcServerController(
    @Inject val grpcEmbeddedServer: GrpcEmbeddedServer
) {

    @Get("/api/stop-grpc")
    fun stop(): HttpResponse<String> {
        grpcEmbeddedServer.stop()
        return HttpResponse.ok("is-running? ${grpcEmbeddedServer.isRunning}")
    }
}