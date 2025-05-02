
create or replace procedure proc_deletar_cadastro_economico(p_id in numeric) is
begin
delete from enquadramentoambiental where cadastroeconomico_id = p_id;
delete from CADASTROECONOMICOHIST where cadastroeconomico_id = p_id;
delete from ENQUADRAMENTOFISCAL where cadastroeconomico_id = p_id;
delete from ENDERECOCADASTROECONOMICO where cadastroeconomico_id = p_id;
delete from ISENCAOCADASTROECONOMICO where cadastroeconomico_id = p_id;
delete from PLACAAUTOMOVELCMC where cadastroeconomico_id = p_id;
delete from BCECARACFUNCIONAMENTO where cadastroeconomico_id = p_id;
delete from CE_ARQUIVOS where cadastroeconomico_id = p_id;
delete from SOCIEDADECADASTROECONOMICO where cadastroeconomico_id = p_id;
delete from CE_REGISTROJUNTAS where cadastroeconomico_id = p_id;
delete from CE_SITUACAOCADASTRAL where cadastroeconomico_id = p_id;
delete from TIPOPROCESSOECONOMICO where cadastroeconomico_id = p_id;
delete from CE_VALORATRIBUTOS where cadastroeconomico_id = p_id;
delete from CADASTROECONOMICO_SERVICO where cadastroeconomico_id = p_id;
delete from ECONOMICOCNAE where cadastroeconomico_id = p_id;
delete from CERTIDAOATIVIDADEBCE where cadastroeconomico_id = p_id;
delete from HISTORICO where cadastro_id = p_id;
delete from CADASTROECONOMICO where id = p_id;
delete from CADASTRO where id = p_id;
end;
