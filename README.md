The project is supposed to be an example of how to do throttling in
an application that uses Akka.

# Throttling

There is a simple processing chain implemented with Akka: There are:

- Sender, who produces messages and sends them to Processor;
- Processor, who does some processing (basically sleeps for 10 ms) and
then gives those messages to Receiver
- Receiver who receives messages and prints statistics from time to time.

The problem is that Sender is able to send messages much faster than
Processor can process them and sooner or later the whole thing would
crash because of out of memory. To solve it there is a Throttler,
who receives messages from Receiver and tries to analyse if Sender is
sending them faster than Receiver is receiving. In that case Throttler would
ask Sender to slow down (and speed up in the opposite case). The whole
system should balance itself after a while and process those messages with
maxinum speed of the slowest component.

## Metrics

There are metrics that Prometheus could scrape.
They are available at port 8001 by default.