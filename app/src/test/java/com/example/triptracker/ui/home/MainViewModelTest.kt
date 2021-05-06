package com.example.triptracker.ui.home

//import io.fabric8.mockwebserver.DefaultMockServer
import com.example.triptracker.InstantExecutorExtension
import com.example.triptracker.data.remote.WebSocketRideCallBack
import com.example.triptracker.data.remote.model.VehicleLocation
import com.example.triptracker.data.repository.TripRepo
import com.example.triptracker.helpers.rx.BaseRxBus
import com.google.maps.GeoApiContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.Assertions
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


//    @get:Rule
//    var rule: TestRule = InstantTaskExecutorRule()

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    lateinit var mockWebSocket: WebSocket

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    lateinit var request: Request

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private lateinit var mockClient: OkHttpClient

    @Mock
    private lateinit var geoContext: GeoApiContext

    @Mock
    private lateinit var rxBus: BaseRxBus

    lateinit var tripRepo: TripRepo
    val server = MockWebServer()
    lateinit var viewModel: MainViewModel


    @BeforeAll
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        tripRepo = TripRepo(geoContext, mockClient, request)
        viewModel = MainViewModel(tripRepo)

    }

    @Test
    fun test1() {
        //given
        mockWebSocket(getLocationChangedEventStr())
        val expectedValue = getLocationChangedEvent()

        ///when
        val updates = viewModel.getRideUpdatesObservable()

        //then
        val actulValue = updates.vehicleLocation.value
        Assertions.assertEquals(expectedValue, actulValue)

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

    private fun getLocationChangedEventStr() = "{\n" +
            "      \"event\": \"vehicleLocationUpdated\",\n" +
            "      \"data\": {\n" +
            "        \"address\": null,\n" +
            "        \"lng\": 10.1,\n" +
            "        \"lat\": 20.2\n" +
            "      }\n" +
            "    }"

    private fun getLocationChangedEvent() = VehicleLocation(null, 20.2, 10.1)

    //endregion

}

