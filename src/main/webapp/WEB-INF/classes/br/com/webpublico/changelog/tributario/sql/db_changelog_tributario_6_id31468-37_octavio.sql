update calculoalvara set controlecalculo = 'CALCULO' where controlecalculo = 'CALCULO_ATUAL';
update calculoalvara set controlecalculo = 'FINALIZADO' where controlecalculo in ('CALCULO_FINALIZADO', 'CALCULO_CANCELADO');
