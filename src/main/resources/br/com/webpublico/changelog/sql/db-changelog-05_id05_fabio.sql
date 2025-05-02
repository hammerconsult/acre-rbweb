update calculoitbi set tipobasecalculoitbi = 'VALOR_VENAL' where tipobasecalculoitbi = 'VALOR_CALCULADO';
update calculoitbi set tipobasecalculoitbi = 'VALOR_VENAL_ACRESCIMOS' where tipobasecalculoitbi = 'VALOR_CALCULADO_REAJUSTE';
update calculoitbi set tipobasecalculoitbi = 'VALOR_APURADO' where tipobasecalculoitbi = 'VALOR_NEGOCIADO';
update calculoitbi set tipobasecalculoitbi = 'VALOR_NEGOCIADO' where tipobasecalculoitbi = 'VALOR_INFORMADO';
update parametrositbi set tipobasecalculo = 'VALOR_VENAL' where tipobasecalculo = 'VALOR_CALCULADO';
update parametrositbi set tipobasecalculo = 'VALOR_VENAL_ACRESCIMOS' where tipobasecalculo = 'VALOR_CALCULADO_REAJUSTE';
update parametrositbi set tipobasecalculo = 'VALOR_APURADO' where tipobasecalculo = 'VALOR_NEGOCIADO';
update parametrositbi set tipobasecalculo = 'VALOR_NEGOCIADO' where tipobasecalculo = 'VALOR_INFORMADO';