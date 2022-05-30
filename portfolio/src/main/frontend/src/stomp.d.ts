/**
 * Copy type declarations from the 'stompjs' library to the web-specific stompjs/lib/stompjs library.
 */
declare module 'stompjs/lib/stomp' {
    import StompImpl from 'stompjs'

    export import Stomp = StompImpl
}