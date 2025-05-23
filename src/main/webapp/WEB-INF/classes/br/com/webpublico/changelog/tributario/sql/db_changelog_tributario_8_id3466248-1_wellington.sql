begin
for registro in (select CADASTROIMOBILIARIO_ID, DATATRANSFERENCIA, NUMEROPROTOCOLO, count(1), max(id) as id
                     from transferenciaproprietario
                     group by CADASTROIMOBILIARIO_ID, DATATRANSFERENCIA, NUMEROPROTOCOLO
                     having count(1) > 1)
        loop
delete
from PESSOATRANSFPROPANT
where TRANSFERENCIAPROPRIETARIO_ID in (select id
                                       from transferenciaproprietario
                                       where CADASTROIMOBILIARIO_ID = registro.CADASTROIMOBILIARIO_ID
                                         and DATATRANSFERENCIA = registro.DATATRANSFERENCIA
                                         and NUMEROPROTOCOLO = registro.NUMEROPROTOCOLO
                                         and id != registro.ID);

delete
from PESSOATRANSFPROPRIETARIO
where TRANSFERENCIAPROPRIETARIO_ID in (select id
                                       from transferenciaproprietario
                                       where CADASTROIMOBILIARIO_ID = registro.CADASTROIMOBILIARIO_ID
                                         and DATATRANSFERENCIA = registro.DATATRANSFERENCIA
                                         and NUMEROPROTOCOLO = registro.NUMEROPROTOCOLO
                                         and id != registro.ID);

delete
from transferenciaproprietario
where CADASTROIMOBILIARIO_ID = registro.CADASTROIMOBILIARIO_ID
  and DATATRANSFERENCIA = registro.DATATRANSFERENCIA
  and NUMEROPROTOCOLO = registro.NUMEROPROTOCOLO
  and id != registro.ID;
end loop;
end;
