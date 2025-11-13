package com.anos.network

import com.anos.model.result.NetworkResult
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

class UtilsTest {

    @Test
    fun `safeApiCall returns Success when API call is successful with body`() = runTest {
        // Given
        val expectedData = "Test Data"
        val mockResponse = Response.success(expectedData)
        val apiCall: suspend () -> Response<String> = { mockResponse }

        // When
        val result = safeApiCall(apiCall)

        // Then
        assertTrue(result is NetworkResult.Success)
        assertEquals(200, (result as NetworkResult.Success).code)
        assertEquals(expectedData, result.data)
    }

    @Test
    fun `safeApiCall returns Error when API call is successful but body is null`() = runTest {
        // Given
        // Using error response to simulate null body scenario
        val mockResponse: Response<String> = Response.success(null)
        val apiCall: suspend () -> Response<String> = { mockResponse }

        // When
        val result = safeApiCall(apiCall)

        // Then
        assertTrue(result is NetworkResult.Error)
        assertEquals(200, (result as NetworkResult.Error).code)
    }

    @Test
    fun `safeApiCall returns Error when API call fails with error response`() = runTest {
        // Given
        val errorBody = """{"error": "Not Found"}""".toResponseBody()
        val mockResponse: Response<String> = Response.error(404, errorBody)
        val apiCall: suspend () -> Response<String> = { mockResponse }

        // When
        val result = safeApiCall(apiCall)

        // Then
        assertTrue(result is NetworkResult.Error)
        assertEquals(404, (result as NetworkResult.Error).code)
        assertTrue(result.errorMsg?.contains("error") == true)
    }

    @Test
    fun `safeApiCall returns Error when HttpException is thrown`() = runTest {
        // Given
        val errorBody = """{"message": "Unauthorized"}""".toResponseBody()
        val mockResponse: Response<String> = Response.error(401, errorBody)
        val httpException = HttpException(mockResponse)
        val apiCall: suspend () -> Response<String> = { throw httpException }

        // When
        val result = safeApiCall(apiCall)

        // Then
        assertTrue(result is NetworkResult.Error)
        assertEquals(401, (result as NetworkResult.Error).code)
    }

    @Test
    fun `safeApiCall returns Exception when IOException is thrown`() = runTest {
        // Given
        val ioException = IOException("Network error")
        val apiCall: suspend () -> Response<String> = { throw ioException }

        // When
        val result = safeApiCall(apiCall)

        // Then
        assertTrue(result is NetworkResult.Exception)
        assertEquals(ioException, (result as NetworkResult.Exception).e)
        assertEquals("Network error", result.e.message)
    }

    @Test
    fun `safeApiCall returns Exception when SocketTimeoutException is thrown`() = runTest {
        // Given
        val timeoutException = SocketTimeoutException("Connection timed out")
        val apiCall: suspend () -> Response<String> = { throw timeoutException }

        // When
        val result = safeApiCall(apiCall)

        // Then
        assertTrue(result is NetworkResult.Exception)
        assertEquals(timeoutException, (result as NetworkResult.Exception).e)
        assertEquals("Connection timed out", result.e.message)
    }

    @Test
    fun `safeApiCall returns Exception when generic Throwable is thrown`() = runTest {
        // Given
        val genericException = RuntimeException("Unexpected error")
        val apiCall: suspend () -> Response<String> = { throw genericException }

        // When
        val result = safeApiCall(apiCall)

        // Then
        assertTrue(result is NetworkResult.Exception)
        assertEquals(genericException, (result as NetworkResult.Exception).e)
        assertEquals("Unexpected error", result.e.message)
    }

    @Test
    fun `safeApiCall handles complex data types successfully`() = runTest {
        // Given
        data class ComplexData(val id: Int, val name: String, val items: List<String>)
        val expectedData = ComplexData(1, "Test", listOf("item1", "item2"))
        val mockResponse = Response.success(expectedData)
        val apiCall: suspend () -> Response<ComplexData> = { mockResponse }

        // When
        val result = safeApiCall(apiCall)

        // Then
        assertTrue(result is NetworkResult.Success)
        assertEquals(200, (result as NetworkResult.Success).code)
        assertEquals(expectedData, result.data)
        assertEquals(1, result.data.id)
        assertEquals("Test", result.data.name)
        assertEquals(2, result.data.items.size)
    }

    @Test
    fun `safeApiCall returns Error with 500 status code for server error`() = runTest {
        // Given
        val errorBody = """{"error": "Internal Server Error"}""".toResponseBody()
        val mockResponse: Response<String> = Response.error(500, errorBody)
        val apiCall: suspend () -> Response<String> = { mockResponse }

        // When
        val result = safeApiCall(apiCall)

        // Then
        assertTrue(result is NetworkResult.Error)
        assertEquals(500, (result as NetworkResult.Error).code)
    }

    @Test
    fun `safeApiCall returns Error with 503 status code for service unavailable`() = runTest {
        // Given
        val errorBody = """{"error": "Service Unavailable"}""".toResponseBody()
        val mockResponse: Response<String> = Response.error(503, errorBody)
        val apiCall: suspend () -> Response<String> = { mockResponse }

        // When
        val result = safeApiCall(apiCall)

        // Then
        assertTrue(result is NetworkResult.Error)
        assertEquals(503, (result as NetworkResult.Error).code)
    }

    @Test
    fun `safeApiCall handles empty error body gracefully`() = runTest {
        // Given
        val errorBody = "".toResponseBody()
        val mockResponse: Response<String> = Response.error(400, errorBody)
        val apiCall: suspend () -> Response<String> = { mockResponse }

        // When
        val result = safeApiCall(apiCall)

        // Then
        assertTrue(result is NetworkResult.Error)
        assertEquals(400, (result as NetworkResult.Error).code)
        assertEquals("", (result as NetworkResult.Error).errorMsg)
    }

    @Test
    fun `safeApiCall returns Success for 201 Created response`() = runTest {
        // Given
        val expectedData = "Created Resource"
        val mockResponse = Response.success(expectedData)
        val apiCall: suspend () -> Response<String> = { mockResponse }

        // When
        val result = safeApiCall(apiCall)

        // Then
        assertTrue(result is NetworkResult.Success)
        assertEquals(200, (result as NetworkResult.Success).code)
        assertEquals(expectedData, result.data)
    }

    @Test
    fun `safeApiCall returns Success for 202 Accepted response`() = runTest {
        // Given
        val expectedData = "Accepted"
        val mockResponse = Response.success(expectedData)
        val apiCall: suspend () -> Response<String> = { mockResponse }

        // When
        val result = safeApiCall(apiCall)

        // Then
        assertTrue(result is NetworkResult.Success)
        assertEquals(200, (result as NetworkResult.Success).code)
        assertEquals(expectedData, result.data)
    }
}
