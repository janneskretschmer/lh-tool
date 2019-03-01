#!/usr/bin/env python3
import os.path

from gevent.event import Event
from gevent import monkey
monkey.patch_all()

from slimta.relay.pipe import PipeRelay
from slimta.queue.dict import DictStorage
from slimta.queue import Queue
from slimta.edge.smtp import SmtpEdge, SmtpValidators

EMAIL_OUT_DIR = './'
INBOUND_PORT = 465
SERVER_NAME = 'localhost'
MSG_MAX_SIZE = 1024 * 1024 * 10 # 10MB

def _start_inbound_relay():
    return PipeRelay(['tee', os.path.join(EMAIL_OUT_DIR, '{message_id}.eml')])

def _start_inbound_queue(relay):
    envelope_db = {}
    meta_db = {}
    storage = DictStorage(envelope_db, meta_db)
    queue = Queue(storage, relay)
    queue.start()
    return queue

def _start_inbound_edge(queue):
    inbound_banner = '{0} ESMTP test agent'.format(SERVER_NAME)

    class EdgeValidators(SmtpValidators):
        # Explicit method override, therefore not possible to transform to function
        # pylint: disable=R0201
        def handle_banner(self, reply, address):
            print('RCVD')
            reply.message = inbound_banner

    edge = SmtpEdge(('', INBOUND_PORT), queue,
                    max_size=MSG_MAX_SIZE,
                    validator_class=EdgeValidators,
                    command_timeout=20.0,
                    data_timeout=30.0)
    edge.start()
    return edge

def main():
    in_relay = _start_inbound_relay()
    in_queue = _start_inbound_queue(in_relay)
    _start_inbound_edge(in_queue)
    try:
        print("Running on port {0}...".format(INBOUND_PORT))
        Event().wait()
    except KeyboardInterrupt:
        print()

if __name__ == '__main__':
    main()

