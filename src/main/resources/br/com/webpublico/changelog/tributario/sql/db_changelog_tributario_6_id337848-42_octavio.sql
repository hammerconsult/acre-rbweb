declare num_id number;
begin
    select hibernate_sequence.nextval into num_id from dual;
    insert into configuracaopix (id, urlintegrador, base, appkey, urltoken, urlqrcode)
     values (num_id, 'http://172.16.0.74:8086', 'WPPRODUCAO', 'gw-dev-app-key', 'https://oauth.bb.com.br/oauth/token', 'https://api.bb.com.br/pix-bb/v1');
    insert into configuracaopixdam (id, configuracaopix_id, configuracaodam_id, numeroconvenio, chavepix, chaveacesso, authorization)
    values (hibernate_sequence.nextval, num_id, (select id from configuracaodam where codigofebraban = 3646),  '81051', '7a60891e-186b-4738-a733-f4e80c07293e',
            '7091d08b03ffbe00136ae18130050d56b941a5b1',
            'ZXlKcFpDSTZJbUUwTnpnMElpd2lZMjlrYVdkdlVIVmliR2xqWVdSdmNpSTZNQ3dpWTI5a2FXZHZVMjltZEhkaGNtVWlPakl3TkRBM0xDSnpaWEYxWlc1amFXRnNTVzV6ZEdGc1lXTmhieUk2TVgwOmV5SnBaQ0k2SWpNNE5ESTVPV010WVRBMU1TMDBOVFJpTFdGak9HSXRaV0pqTXpobE9EVmhZV1l3WW1Wa1pqa2lMQ0pqYjJScFoyOVFkV0pzYVdOaFpHOXlJam93TENKamIyUnBaMjlUYjJaMGQyRnlaU0k2TWpBME1EY3NJbk5sY1hWbGJtTnBZV3hKYm5OMFlXeGhZMkZ2SWpveExDSnpaWEYxWlc1amFXRnNRM0psWkdWdVkybGhiQ0k2TVN3aVlXMWlhV1Z1ZEdVaU9pSndjbTlrZFdOaGJ5SXNJbWxoZENJNk1UWTBNemc1TmpFeE1UYzFNbjA=');
    insert into configuracaopixdam (id, configuracaopix_id, configuracaodam_id, numeroconvenio, chavepix, chaveacesso, authorization)
    values (hibernate_sequence.nextval, num_id, (select id from configuracaodam where codigofebraban = 209), '93567', '49c62a0b-90f1-4d5c-8fe2-adfd7fd57f38',
            '7091008b08ffbef0136ce181f0050c56b961a5b3',
            'ZXlKcFpDSTZJakZrWW1NeE5USXRPREZrTXkwMFpEa3lMV0ZoWWpJdE15SXNJbU52WkdsbmIxQjFZbXhwWTJGa2IzSWlPakFzSW1OdlpHbG5iMU52Wm5SM1lYSmxJam95TmpnNU55d2ljMlZ4ZFdWdVkybGhiRWx1YzNSaGJHRmpZVzhpT2pGOTpleUpwWkNJNkltVTNNRFU1T1RRdFlqazRPUzBpTENKamIyUnBaMjlRZFdKc2FXTmhaRzl5SWpvd0xDSmpiMlJwWjI5VGIyWjBkMkZ5WlNJNk1qWTRPVGNzSW5ObGNYVmxibU5wWVd4SmJuTjBZV3hoWTJGdklqb3hMQ0p6WlhGMVpXNWphV0ZzUTNKbFpHVnVZMmxoYkNJNk1Td2lZVzFpYVdWdWRHVWlPaUp3Y205a2RXTmhieUlzSW1saGRDSTZNVFkxTWpNd01UTXdOVEl5TVgw');

    select hibernate_sequence.nextval into num_id from dual;
    insert into configuracaopix (id, urlintegrador, base, appkey, urltoken, urlqrcode)
    values (num_id, 'http://172.16.0.74:8086', 'PREPRODUCAO', 'gw-dev-app-key', 'https://oauth.hm.bb.com.br/oauth/token', 'https://api.hm.bb.com.br/pix-bb/v1');
    insert into configuracaopixdam (id, configuracaopix_id, configuracaodam_id, numeroconvenio, chavepix, chaveacesso, authorization)
    values (hibernate_sequence.nextval, num_id, (select id from configuracaodam where codigofebraban = 3646), '81051', '94390a64-d9ee-46e9-a240-420e3aa449fb',
            'd27b377909ffabd0136ce17dd0050e56b971a5be',
            'ZXlKcFpDSTZJbVJrWW1ZMFkyWXRNakZsTnkwME9HVm1MVGsxTUdNdFpEbGhZVE1pTENKamIyUnBaMjlRZFdKc2FXTmhaRzl5SWpvd0xDSmpiMlJwWjI5VGIyWjBkMkZ5WlNJNk1qUXhOaklzSW5ObGNYVmxibU5wWVd4SmJuTjBZV3hoWTJGdklqb3hmUTpleUpwWkNJNklqRmlPVFpsWkRBdFltRTFZeTAwTlRVM0xXSXdPVFV0TmpRNU9EaGtPRFJsT0Rjek5EWm1OemMzTVdFdE1EYzBPUzBpTENKamIyUnBaMjlRZFdKc2FXTmhaRzl5SWpvd0xDSmpiMlJwWjI5VGIyWjBkMkZ5WlNJNk1qUXhOaklzSW5ObGNYVmxibU5wWVd4SmJuTjBZV3hoWTJGdklqb3hMQ0p6WlhGMVpXNWphV0ZzUTNKbFpHVnVZMmxoYkNJNk1Td2lZVzFpYVdWdWRHVWlPaUpvYjIxdmJHOW5ZV05oYnlJc0ltbGhkQ0k2TVRZek5URTVOamN5TXpJeE9YMA==');
    insert into configuracaopixdam (id, configuracaopix_id, configuracaodam_id, numeroconvenio, chavepix, chaveacesso, authorization)
    values (hibernate_sequence.nextval, num_id, (select id from configuracaodam where codigofebraban = 209), '93567', '94390a64-d9ee-46e9-a240-420e3aa449fb',
            'd27bb7790cffabf0136ae17d90050456b941a5b2',
            'ZXlKcFpDSTZJamRqTVRkaE1URWlMQ0pqYjJScFoyOVFkV0pzYVdOaFpHOXlJam93TENKamIyUnBaMjlUYjJaMGQyRnlaU0k2TXpRMk5UTXNJbk5sY1hWbGJtTnBZV3hKYm5OMFlXeGhZMkZ2SWpveGZROmV5SnBaQ0k2SWprMlltRWlMQ0pqYjJScFoyOVFkV0pzYVdOaFpHOXlJam93TENKamIyUnBaMjlUYjJaMGQyRnlaU0k2TXpRMk5UTXNJbk5sY1hWbGJtTnBZV3hKYm5OMFlXeGhZMkZ2SWpveExDSnpaWEYxWlc1amFXRnNRM0psWkdWdVkybGhiQ0k2TVN3aVlXMWlhV1Z1ZEdVaU9pSm9iMjF2Ykc5bllXTmhieUlzSW1saGRDSTZNVFkxTVRjMk5UQTFPREkzT1gw');
end;
