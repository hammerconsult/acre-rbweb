delete from templatenfse where tipotemplate = 'ATIVACAO_CADASTRO';
insert into templatenfse values (hibernate_sequence.nextval, 'ATIVACAO_CADASTRO',
'<p>Você realizou seu cadastro no sistema <span style="font-weight: bold;">WP ISS
</span> e recebeu a mensagem para ativação do cadastro.<br>Clique no endereço abaixo para que seu cadastro seja
ativado.<br><br>$LINK<br><br>Mensagem enviada automaticamente. Por favor, não responda.<br>Atenciosamente,
<br><br>Prefeitura Municipal de $MUNICIPIO<br></p>');
