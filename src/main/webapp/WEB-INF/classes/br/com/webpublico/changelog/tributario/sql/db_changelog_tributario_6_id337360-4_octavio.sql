update configuracaotributario
set rodapepadraoemail = 'Atenciosamente,<br/>Suporte da Prefeitura Municipal de Rio Branco<br/>Telefone para contato: (68) 3211-3974<br/>Este é um e-mail automático. Por favor não o responda.'
where vigencia = (select c.vigencia from configuracaotributario c order by c.vigencia desc fetch first 1 rows only)
