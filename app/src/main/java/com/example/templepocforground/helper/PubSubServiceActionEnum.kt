package com.example.templepocforground.helper

enum class PubSubServiceActionEnum (val action: String) {
    START_ALERT("START_ALERT"),
    STOP_ALERT("STOP_ALERT"),
    MUTE_ALERT("MUTE_ALERT"),
    OPEN_APP("OPEN_APP"),

    OPEN("ACTION_OPEN"),
    CLOSE("ACTION_CLOSE"),
    MUTE("ACTION_MUTE")
}