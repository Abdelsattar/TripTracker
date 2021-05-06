package com.example.triptracker

object WebSocketMessageFactory {

    fun getBookingOpenedEvent() =
        "{\"event\":\"bookingOpened\",\"data\":{\"status\":\"waitingForPickup\",\"vehicleLocation\":{\"address\":null,\"lng\":13.426789,\"lat\":52.519061},\"pickupLocation\":{\"address\":\"Volksbühne Berlin\",\"lng\":13.411632,\"lat\":52.52663},\"dropoffLocation\":{\"address\":\"Gendarmenmarkt\",\"lng\":13.393208,\"lat\":52.513763},\"intermediateStopLocations\":[{\"address\":\"The Sixties Diner\",\"lng\":13.399356,\"lat\":52.523728},{\"address\":\"Friedrichstraße Station\",\"lng\":13.388238,\"lat\":52.519485}]}}\n"

    fun getLocationChangedEvent() = "{\n" +
            "      \"event\": \"vehicleLocationUpdated\",\n" +
            "      \"data\": {\n" +
            "        \"address\": null,\n" +
            "        \"lng\": 10.1,\n" +
            "        \"lat\": 20.2\n" +
            "      }\n" +
            "    }"


    fun getStopsChangedEvent() = " {\n" +
            "      \"event\": \"intermediateStopLocationsChanged\",\n" +
            "      \"data\": [\n" +
            "        {\n" +
            "          \"lat\": 10.1,\n" +
            "          \"lng\": 20.2,\n" +
            "          \"address\": \"Street Address of Some Stop On The Way\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"lat\": 30.3,\n" +
            "          \"lng\": 40.4,\n" +
            "          \"address\": \"Another Street Address of Some Other Stop On The Way\"\n" +
            "        }\n" +
            "      ]\n" +
            "    }"

    fun getRideStatusEvent() = "  {\n" +
            "      \"event\": \"statusUpdated\",\n" +
            "      \"data\": \"inVehicle\"\n" +
            "    }"

    fun getBookingClosedEvent() = " {\n" +
            "      \"event\": \"bookingClosed\",\n" +
            "      \"data\": null\n" +
            "    }"

}