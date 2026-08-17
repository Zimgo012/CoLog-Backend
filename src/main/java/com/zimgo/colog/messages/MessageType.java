package com.zimgo.colog.messages;

public enum MessageType {

    /* To track user presence*/
    PRESENCE,

    /* Chat type message - will send a message to the diary room */
    CHAT,
    /* Leave and join message - will notify the diary room  */
    LEAVE,
    JOIN,

    /* File type message - will send a file to the diary room*/
    FILE,

    /* Crdt update message using y.js payload */
    YJSUPDATE
}
