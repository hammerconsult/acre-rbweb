update movimentoitemcontrato set tipomovimento = 'ORIGINAL'
where tipomovimento = 'CONTRATO';

update execucaocontratotipofonte set tipomovimento = 'ORIGINAL'
where tipomovimento is null
