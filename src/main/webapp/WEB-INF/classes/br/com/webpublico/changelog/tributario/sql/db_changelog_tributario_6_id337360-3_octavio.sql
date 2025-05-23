update configuracaotributario
set rodapepadraoemail = 'Atenciosamente,<br/>Divisão de Controle e Atendimentos ao Público<br/>Superintendência Municipal de Transportes e Trânsito - RBTRANS<br/>Telefone para contato: +55 (68) 3214-3311<br/>Este é um e-mail automático. Por favor não o responda.',
    rodaperbtransemail = '<div>Atenciosamente,</div><div>Divisão de Controle e Atendimentos ao Público</div><div>Superintendência Municipal de Transportes e Trânsito - RBTRANS</div>Telefone para contato: +55 (68) 3214-3311<br/><br/>Este é um e-mail automático. Por favor não o responda.'
where vigencia = (select c.vigencia from configuracaotributario c order by c.vigencia desc fetch first 1 rows only)
