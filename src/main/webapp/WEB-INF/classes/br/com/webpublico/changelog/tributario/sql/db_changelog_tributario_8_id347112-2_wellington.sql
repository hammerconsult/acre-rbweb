CREATE OR REPLACE FUNCTION FUNCEXISTEMOVCMC(P_ID_CMC IN NUMBER)
    RETURN NUMBER
AS
    RESULTADO NUMBER;
BEGIN
FOR REGISTRO IN (  select cc.TABLE_NAME, cc.COLUMN_NAME
                       from user_constraints c
                                inner join user_cons_columns cc on cc.CONSTRAINT_NAME = c.CONSTRAINT_NAME
                                inner join user_constraints cr on cr.CONSTRAINT_NAME = c.r_CONSTRAINT_NAME
                       where cr.TABLE_NAME IN ('CADASTRO', 'CADASTROECONOMICO')
                         and cc.TABLE_NAME not in ('CADASTRO', 'CADASTROECONOMICO', 'HISTORICO', 'ECONOMICOCNAE', 'CADASTROECONOMICO_SERVICO',
                                                   'CE_VALORATRIBUTOS', 'TIPOPROCESSOECONOMICO', 'CE_SITUACAOCADASTRAL', 'CE_REGISTROJUNTAS', 'SOCIEDADECADASTROECONOMICO',
                                                   'CE_ARQUIVOS', 'BCE_CARACFUNCIONAMENTO', 'PLACAAUTOMOVELCMC', 'ISENCAOCADASTROECONOMICO', 'ENDERECOCADASTROECONOMICO',
                                                   'ENQUADRAMENTOFISCAL', 'CADASTROECONOMICOHIST', 'ENQUADRAMENTOAMBIENTAL'))
    LOOP
       RESULTADO := FUNCCONSULTAREGISTRO(REGISTRO.TABLE_NAME,
                                         REGISTRO.COLUMN_NAME, P_ID_CMC);
       IF (RESULTADO = 1) THEN
           RETURN 1;
END IF;
END LOOP;

RETURN 0;
END;
