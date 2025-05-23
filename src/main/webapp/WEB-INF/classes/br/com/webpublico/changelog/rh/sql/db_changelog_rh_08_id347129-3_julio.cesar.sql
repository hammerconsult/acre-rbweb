update tipoafastamento set intervaloprorrogacaodias = 365 where codigo = '6';
update tipoafastamento set intervaloprorrogacaodias = 1 where codigo != '6';
