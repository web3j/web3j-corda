package org.web3j.corda.protocol

import mu.KLogging
import java.lang.reflect.InvocationHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.Arrays
import javax.ws.rs.ClientErrorException

/**
 * Invocation handler for proxied Corda API resources. Implements an exception mapping mechanism to avoid reporting
 * [ClientErrorException]s to the client.
 */
internal class ClientErrorHandler<T>(
    private val client: T,
    private val mapper: (ClientErrorException) -> RuntimeException
) : InvocationHandler {

    override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any? {
        logger.debug { "Invoking method $method with arguments ${Arrays.toString(args)}" }

        try {
            // Invoke the original method on the client
            return method.invoke(client, *(args ?: arrayOf())).let {
                if (Proxy.isProxyClass(it.javaClass)) {
                    // The result is a Jersey web resource
                    // so we need to wrap it again
                    Proxy.newProxyInstance(
                        method.returnType.classLoader,
                        arrayOf(method.returnType),
                        ClientErrorHandler(it, mapper)
                    )
                } else {
                    it
                }
            }
        } catch (e: InvocationTargetException) {
            throw handleInvocationException(e, method)
        } catch (e: ClientErrorException) {
            throw handleClientError(e, method)
        }
    }

    private fun handleInvocationException(error: InvocationTargetException, method: Method): Throwable {
        return error.targetException.let {
            if (it is ClientErrorException) {
                handleClientError(it, method)
            } else {
                logger.error {
                    "Unexpected exception while invoking method $method: " +
                        (error.message ?: error::class.java.canonicalName)
                }
                error.targetException
            }
        }
    }

    private fun handleClientError(error: ClientErrorException, method: Method): RuntimeException {
        logger.error {
            "Client exception while invoking method $method: " +
                (error.message ?: error.response.statusInfo.reasonPhrase)
        }
        return mapper.invoke(error)
    }

    companion object : KLogging()
}
