package com.example.triptracker.ui.home

import com.example.triptracker.InstantExecutorExtension
import com.example.triptracker.WebSocketDataFactory
import com.example.triptracker.WebSocketDataFactory.getLocationChangedData
import com.example.triptracker.WebSocketMessageFactory
import com.example.triptracker.WebSocketMessageFactory.getLocationChangedEvent
import com.example.triptracker.data.remote.WebSocketRideCallBack
import com.example.triptracker.data.repository.TripRepo
import com.example.triptracker.helpers.rx.BaseRxBus
import com.google.maps.GeoApiContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.invocation.InvocationOnMock
import org.mockito.kotlin.any


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
internal class MainViewModelTest {


    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    lateinit var mockWebSocket: WebSocket

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private lateinit var mockClient: OkHttpClient

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    lateinit var request: Request

    @Mock
    private lateinit var geoContext: GeoApiContext

    @Mock
    private lateinit var rxBus: BaseRxBus

    lateinit var tripRepo: TripRepo


    lateinit var viewModel: MainViewModel


    @BeforeAll
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        tripRepo = TripRepo(geoContext, mockClient, request)
        viewModel = MainViewModel(tripRepo)
    }


    @Test
    fun `should update booking opened when get booking opened from webSocket `() {
        //given
        mockWebSocket(WebSocketMessageFactory.getBookingOpenedEvent())
        val expectedValue = WebSocketDataFactory.getBookingOpenedData()

        ///when
        val updates = viewModel.startRide()

        //then
        val actualValue = updates.bookingOpened.value
        assertEquals(expectedValue, actualValue)

    }

    @Test
    fun `should update vehicleLocation when get location update event from webSocket `() {
        //given
        mockWebSocket(getLocationChangedEvent())
        val expectedValue = getLocationChangedData()

        ///when
        val updates = viewModel.startRide()

        //then
        val actualValue = updates.vehicleLocation.value
        assertEquals(expectedValue, actualValue)

    }

    @Test
    fun `should update ride status  when get status changed  event from webSocket `() {
        //given
        mockWebSocket(WebSocketMessageFactory.getRideStatusEvent())
        val expectedValue = WebSocketDataFactory.getRideStatusData()

        ///when
        val updates = viewModel.startRide()

        //then
        val actualValue = updates.statusUpdated.value
        assertEquals(expectedValue, actualValue)

    }

    @Test
    fun `should update stops when get stops changes event changed from webSocket `() {
        //given
        mockWebSocket(WebSocketMessageFactory.getStopsChangedEvent())
        val expectedValue = WebSocketDataFactory.getStopsChangedData()

        ///when
        val updates = viewModel.startRide()

        //then
        val actualValue = updates.stopsChanges.value
        assertEquals(expectedValue, actualValue)

    }


    @Test
    fun `should update stops when get ride closed event changed from webSocket `() {
        //given
        mockWebSocket(WebSocketMessageFactory.getBookingClosedEvent())
        val expectedValue = WebSocketDataFactory.getBookingClosedData()

        ///when
        val updates = viewModel.startRide()

        //then
        val actualValue = updates.bookingClosed.value
        assertEquals(expectedValue, actualValue)

    }

    //region helper functions
    private fun mockWebSocket(message: String) {
        `when`(
            mockClient.newWebSocket(any(), any())
        ).thenAnswer { newWebSocket: InvocationOnMock ->
            val wsl: WebSocketRideCallBack =
                newWebSocket.getArgument(1, WebSocketRideCallBack::class.java)

            wsl.onMessage(mockWebSocket, message)
            mockWebSocket
        }
    }

    //endregion

}

