delete
from enderecocadastroeconomico ece
where tipoendereco != 'COMERCIAL'
  and nullif(trim(bairro), '') is null
  and nullif(trim(logradouro), '') is null
  and nullif(trim(numero), '') is null
  and nullif(trim(complemento), '') is null;

delete
from enderecocalculoalvara
where tipoendereco != 'COMERCIAL'
  and nullif(trim(bairro), '') is null
  and nullif(trim(logradouro), '') is null
  and nullif(trim(numero), '') is null
  and nullif(trim(complemento), '') is null;

delete
from enderecoalvara ece
where tipoendereco != 'COMERCIAL'
  and nullif(trim(bairro), '') is null
  and nullif(trim(logradouro), '') is null
  and nullif(trim(numero), '') is null
  and nullif(trim(complemento), '') is null;
