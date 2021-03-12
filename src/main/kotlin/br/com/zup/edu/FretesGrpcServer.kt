package br.com.zup.edu

import com.google.protobuf.Any
import com.google.rpc.Code
import io.grpc.Status
import io.grpc.protobuf.StatusProto
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class FretesGrpcServer : FretesServiceGrpc.FretesServiceImplBase() {
    private val logger = LoggerFactory.getLogger(FretesGrpcServer::class.java)

    override fun calculaFrete(request: CalculaFreteRequest?, responseObserver: StreamObserver<CalculaFreteResponse>?) {
        logger.info("Calculando frete para request: ${request}")

        val cep = request?.cep
        if (cep == null || cep.isBlank()) {
            val error = Status.INVALID_ARGUMENT
                .withDescription("cep deve ser informado")
                .asRuntimeException()
            responseObserver?.onError(error)
            return
        }

        if (!cep.matches("[0-9]{5}-[0-9]{3}".toRegex())) {
            val error = Status.INVALID_ARGUMENT
                .withDescription("cep inválido")
                .augmentDescription("formato esperado deve ser 99999-999")
                .asRuntimeException()
            responseObserver?.onError(error)
            return
        }

        // SIMULAR uma verificação de segurança
        if (cep.endsWith("333")) {
            val statusProto = com.google.rpc.Status.newBuilder()
                .setCode(Code.PERMISSION_DENIED.number)
                .setMessage("Usuário não pode acessar esse recurso")
                .addDetails(
                    Any.pack(
                        ErrorDetails
                            .newBuilder()
                            .setCode(401)
                            .setMessage("token expirado")
                            .build()
                    )
                )
                .build()

            val e = StatusProto.toStatusRuntimeException(statusProto)
            responseObserver?.onError(e)
            return
        }

        var valor = 0.0
        try {
            valor = Random.nextDouble(from = 0.0, until = 150.0) // Lógica complexa
            if (valor >= 100) {
                throw IllegalArgumentException("Erro inesperado ao executar a lógica complexa.")
            }
        } catch (e: Exception) {
            responseObserver?.onError(
                Status.INTERNAL
                    .withDescription(e.message)
                    .withCause(e)
                    .asRuntimeException()
            )
        }

        val response = CalculaFreteResponse.newBuilder()
            .setCep(request.cep)
            .setValor(valor)
            .build()

        logger.info("Frete calculado: $response")

        responseObserver?.onNext(response)
        responseObserver?.onCompleted()
    }
}