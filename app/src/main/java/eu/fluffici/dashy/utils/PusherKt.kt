package eu.fluffici.dashy.utils

import com.pusher.client.Pusher
import com.pusher.client.channel.ChannelEventListener
import com.pusher.client.channel.PusherEvent
import com.pusher.client.channel.SubscriptionEventListener

inline fun Pusher.subscribe(channel: String, crossinline onEvent: (event: PusherEvent) -> Unit) {
    val listener = SubscriptionEventListener { event -> onEvent(event)}
    this.subscribe(channel, listener as ChannelEventListener?)
}