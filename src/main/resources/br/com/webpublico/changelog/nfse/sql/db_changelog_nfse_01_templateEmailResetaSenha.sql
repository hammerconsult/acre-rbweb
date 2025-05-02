delete from templatenfse where tipotemplate = 'SOLICITACAO_ALTERACAO_SENHA';
insert into templatenfse values (hibernate_sequence.nextval, 'SOLICITACAO_ALTERACAO_SENHA',
'<p>Você solicitou alteração de sua senha de acesso ao sistema <span style="font-weight: bold;">WP ISS
</span>.<br>Clique no endereço para informar uma nova senha
ativado.<br><br>$LINK<br><br>Mensagem enviada automaticamente. Por favor, não responda.<br>Atenciosamente,
<br><br>Prefeitura Municipal de $MUNICIPIO<br></p>');
