INSERT INTO CONFIGAGENDAMENTOTAREFA(ID, HORA, MINUTO, TIPOTAREFAAGENDADA, CRON)
VALUES (HIBERNATE_SEQUENCE.nextval, 0, 0, 'REPROCESSAMENTO_ATUALIZACAO_DADOS_RB_PONTO', '0 0 */8 ? * *');
