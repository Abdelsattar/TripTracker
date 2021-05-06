# TripTracker

# Description
 this is app that simulate a trip from a client side from pickup point to a drop off point using webSocket
 
## How to build the app
- clone the repo
- open the app in android studio
- get your own google map api key
 (follow the directions here: https://developers.google.com/maps/documentation/android/start#get-key)
- create a keys name GoogleMapsAPIKey, WebsocketEndpoint in your local.properties and put your values like below

```
GoogleMapsAPIKey=YOUR_API_KEY
WebsocketEndpoint=YOUR_WEBSOCKET_ENDPOINT
```
- run the application

## What implemented in this app
- Connect to webSocket. 
- Add pickup and dropOff address
- Get direction between vehicle and pick up.
- Draw a path contain vehicle and pickup.
- Get direction between pickup, stops and dropOff.
- Update vehicle current location when updates come from server.
- Animation for moving car.


## What is used in this app
- [Unit testing](https://developer.android.com/training/testing/unit-testing/local-unit-tests)
- [Mockito-Kotlin](https://github.com/mockito/mockito-kotlin)
- [OkHttp](https://square.github.io/okhttp)
- [RXJava3](https://github.com/ReactiveX/RxJava)
- [MVVM](https://developer.android.com/jetpack/docs/guide)
- [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)
- [Maps Sdk for Android](https://developers.google.com/maps/documentation/android-sdk/overview)
- [Direction API](https://developers.google.com/maps/documentation/directions/overview)
- [Dependency Injection (Dagger 2)](https://github.com/google/dagger)
- [Gson](https://github.com/google/gson)



## Expecting Schema from webSocket
Here is the exact schema of the events that will occur:

##Structure
All messages sent to your client by the server will be JSON strings. They all have the following structure:

    {
      "event": "anEventName",
      "data": "checkTheSchemaForIndividualEventsBelow"
    }
    
The event property will tell you the name of the event that is occurring. The data property will either be an object, 
a string, or null. The exact schema for each type of event is described below.

### bookingOpened event
    {
      "event": "bookingOpened",
      "data": {
        "status": "waitingForPickup",
        "vehicleLocation": {
          "address": null,
          "lng": 10.1,
          "lat": 20.2
        },
        "pickupLocation": {
          "address": "The street address of the pickup location",
          "lng": 30.3,
          "lat": 40.4
        },
        "dropoffLocation": {
          "address": "The street address of the dropoff location",
          "lng": 50.5,
          "lat": 60.6
        },
        "intermediateStopLocations": [
          {
            "address": "The intermediate stops that will be made between pickup and dropoff",
            "lng": 70.7,
            "lat": 80.8
          }
        ]
      }
    }
    
This event is sent immediately after connecting, and indicates the booking has just opened.

The initial status will always be "waitingForPickup", but will change later in the ride - see later events for details.
The vehicleLocation indicates the location of the vehicle at the moment the booking is opened. Note that we use a standard address/lat/lng schema for all locations. However, for vehicle locations, the address will always be null.
The pickupLocation and dropoffLocation correspond to the locations where you will be picked up and dropped off. The address will always be specified for these.
The intermediateStopLocations is an array of locations the vehicle will stop between pickup and dropoff. The address will always be specified for these.

### vehicleLocationUpdated event
    {
      "event": "vehicleLocationUpdated",
      "data": {
        "address": null,
        "lng": 10.1,
        "lat": 20.2
      }
    }
    
Every time the vehicle moves, the location will be sent to the client in one of these events. Note the address is always null for vehicle locations.

### statusUpdated event
    {
      "event": "statusUpdated",
      "data": "inVehicle"
    }
    
Indicates the status of the booking has changed. There are three events that a booking goes through in its lifecycle:

waitingForPickup - this is the initial state. The vehicle is on the way to pick up the passenger.
inVehicle - this is the second state. The passenger is in the vehicle and on the way to be dropped off, though the vehicle may make some extra stops on the way.
droppedOff - this is the final state. The passenger has left the vehicle. The booking will automatically be closed after this.

### intermediateStopLocationsChanged event
    {
      "event": "intermediateStopLocationsChanged",
      "data": [
        {
          "lat": 10.1,
          "lng": 20.2,
          "address": "Street Address of Some Stop On The Way"
        },
        {
          "lat": 30.3,
          "lng": 40.4,
          "address": "Another Street Address of Some Other Stop On The Way"
        }
      ]
    }
    
Indicates the list of intermediate stops being made between the pickup and dropoff has changed. This can happen at any time during the booking. It may happen for a couple of reasons:

The vehicle has completed a stop and it has been removed from the list
The vehicle has been assigned a new stop to make and it has been added to the list

### bookingClosed event
    {
      "event": "bookingClosed",
      "data": null
    }
    
This is sent after the dropoff has finished. After this event has been sent, the WebSockets connection will be closed.


## Due to time bound these needed to be handled
- path need to be re-draw when the car go out of predefined path.
- handel when stops change inside the trip. 
- add more unit test for other components.
- add UI test.

