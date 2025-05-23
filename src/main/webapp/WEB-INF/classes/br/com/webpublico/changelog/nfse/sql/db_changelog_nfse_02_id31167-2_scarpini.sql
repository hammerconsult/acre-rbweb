delete
from templatenfse
where TIPOTEMPLATE = 'SOLICITACAO_ALTERACAO_SENHA';
insert into templatenfse (id, tipotemplate, conteudo)
values (hibernate_sequence.nextval, 'SOLICITACAO_ALTERACAO_SENHA',
        '<table class="email-canvas  " style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0; border-collapse: collapse; margin: 0 auto;" width="100%" height="100%" cellspacing="0" cellpadding="0" border="0" bgcolor="#f0f0f0">
    <tbody><tr style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%;">
        <td style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; mso-table-lspace: 0pt; mso-table-rspace: 0pt;" valign="top">
            <center style="width: 100%; -ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%;">
');
update templatenfse
set CONTEUDO = CONTEUDO || '<!--
                    Set the email width. Defined in two places:
                    1. max-width for all clients except Desktop Windows Outlook, allowing the email to squish on narrow but never go wider than 600px.
                    2. MSO tags for Desktop Windows Outlook enforce a 600px width.
                -->
                <div class="email-wrapper" style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; max-width: 600px; margin: auto;">
                    <!--[if (gte mso 9)|(IE)]>
                    <table cellspacing="0" cellpadding="0" border="0" width="600" align="center">
                        <tr>
                            <td>
                    <![endif]-->'
where tipotemplate = 'SOLICITACAO_ALTERACAO_SENHA';
update templatenfse
set CONTEUDO = CONTEUDO || '<table class="email-header" style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0; border-collapse: collapse; max-width: 600px; margin: 0 auto;" width="100%" cellspacing="0" cellpadding="0" border="0" align="center">
                        <tbody><tr style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%;">
                            <td style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-family: "Helvetica Neue", sans-serif; color: #999; font-size: 13px; line-height: 1.6; padding: 20px 0;" align="center">
                                <a href="$LINK" style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; color: #757575; text-decoration: none;"><img id="headerImage" alt="NFS-e" src="http://nota.riobranco.ac.gov.br/assets/images/servicos-online.png" style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; height: auto; line-height: 100%; outline: none; text-decoration: none; -ms-interpolation-mode: bicubic; max-width: 600px !important; border: 0;" width="150"/></a>
                            </td>
                        </tr>
                    </tbody></table>'
where tipotemplate = 'SOLICITACAO_ALTERACAO_SENHA';
update templatenfse
set CONTEUDO = CONTEUDO || '<table class="email-body" style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0; border-collapse: collapse; max-width: 600px; margin: 0 auto;" width="100%" cellspacing="0" cellpadding="0" border="0" bgcolor="#ffffff" align="center">
                        <tbody><tr style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%;">
                            <td style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; mso-table-lspace: 0pt; mso-table-rspace: 0pt;">
                                <table style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0; border-collapse: collapse; margin: 0 auto;" width="100%" cellspacing="0" cellpadding="30" border="0">
                                    <tbody><tr style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%;">
                                        <td class="email-body-content" style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-family: "Helvetica Neue", sans-serif; color: #444; font-size: 14px; line-height: 150%;" valign="top">
                                            <div id="greeting" style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; margin-bottom: 20px; padding-bottom: 25px; border-bottom-width: 1px; border-bottom-color: #eee; border-bottom-style: solid;">
                                                <table style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0; border-collapse: collapse; margin: 0 auto;" width="100%" cellspacing="0" cellpadding="0" border="0">
                                                    <tbody><tr style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%;">'
where tipotemplate = 'SOLICITACAO_ALTERACAO_SENHA';
update templatenfse
set CONTEUDO = CONTEUDO ||
               '
                                                        <td style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; mso-table-lspace: 0pt; mso-table-rspace: 0pt;">
                                                            <h1 class="greeting" style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; font-family: "Helvetica Neue", sans-serif; color: #444; display: block; font-size: 18px; font-weight: 500; line-height: 1.3; margin: 0;">
                                                                Olá $USUARIO, <span class="reason" style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; display: block; font-size: 15px; font-weight: normal; color: #999;"></span></h1><h1 class="greeting" style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; font-family: "Helvetica Neue", sans-serif; color: #444; display: block; font-size: 18px; font-weight: 500; line-height: 1.3; margin: 0;"><br/></h1><h1 class="greeting" style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; font-family: "Helvetica Neue", sans-serif; color: #444; display: block; font-size: 18px; font-weight: 500; line-height: 1.3; margin: 0;"><span class="reason" style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; display: block; font-size: 15px; font-weight: normal; color: #999;">Você solicitou alteração de sua senha de acesso ao sistema de emissão de notas fiscais eletrônicas do município de Rio Branco.
                                                                </span>
                                                            </h1>
                                                        </td>
                                                        <td class="user-avatar" style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; mso-table-lspace: 0pt; mso-table-rspace: 0pt;" width="58" align="right">
                                                        <br/></td>
                                                    </tr>
                                                </tbody></table>
                                            </div>'
where tipotemplate = 'SOLICITACAO_ALTERACAO_SENHA';
update templatenfse
set CONTEUDO = CONTEUDO || '<p style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; margin-top: 15px; margin-bottom: 15px; font-size: 16px; line-height: 1.5; color: #555;">
                                                A Requisição de alteração de senha foi realizada com sucesso.
                                            </p><p>
                                            </p><p style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; margin-top: 15px; margin-bottom: 15px; font-size: 16px; line-height: 1.5; color: #555;">
                                                Para continuar e criar uma nova senha pressione o botão abaixo, informe
                                                uma nova senha e realize o login.
                                            </p>
                                            <table class="button-table " style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0; border-collapse: collapse; margin: auto;" cellspacing="0" cellpadding="0" border="0" align="center">
                                                <tbody><tr style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%;">
                                                    <td class="button-td" style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-radius: 4px; -webkit-transition: all 100ms ease-in; transition: all 100ms ease-in;" bgcolor="#ea4c89" align="center">
                                                        <a class="button-a" href="$LINK" style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; font-family: "Helvetica Neue", sans-serif; color: #3a8bbb; font-weight: 500; text-decoration: none; background-color: #3d79b0; font-size: 13px; line-height: 1.1; text-align: center; display: block; border-radius: 4px; -webkit-transition: all 100ms ease-in; transition: all 100ms ease-in; border: 15px solid #3d79b0;">
        <span style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; color: #ffffff;">
             Reiniciar Senha
        </span>'
where tipotemplate = 'SOLICITACAO_ALTERACAO_SENHA';
update templatenfse
set CONTEUDO = CONTEUDO || '</a></td>
                                                </tr>
                                            </tbody></table>
                                            <p style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; margin-top: 30px; margin-bottom: 15px; font-size: 12px; line-height: 1.5; color: #555;">
                                                Mensagem enviada automaticamente. Por favor, não responda. <br style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%;"/>
                                                Atenciosamente,<br style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%;"/>
                                                Departamento de Tributação</p>
                                        </td>
                                    </tr>
                                </tbody></table>
                            </td>
                        </tr>
                    </tbody></table>'
where tipotemplate = 'SOLICITACAO_ALTERACAO_SENHA';
update templatenfse
set CONTEUDO = CONTEUDO || '
                    <table class="email-footer" style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0; border-collapse: collapse; max-width: 600px; margin: 0 auto;" width="100%" cellspacing="0" cellpadding="0" border="0" align="center">
                        <tbody><tr style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%;">
                            <td style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-family: "Helvetica Neue", sans-serif; color: #999; font-size: 13px; line-height: 1.6; padding: 30px 5%;" align="center">
                                <div class="address" style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; margin-top: 10px;">
                                    <p>Prefeitura de Rio Branco - Horário de Atendimento: 08h às 18h</p>
                                    <p>Rua Rui Barbosa, 285 – Centro - Rio Branco/AC - CEP: 69.900-901 - Tel.: (68)
                                        3212-7040</p>
                                </div>
                                <br style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%;"/><br style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%;"/>
                                <a href="http://nota.riobranco.ac.gov.br" style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; color: #757575;"><img alt="Dribbble" src="https://nfsetributech.wphomologacao.com.br/assets/images/logo-wp-62.png" style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; height: auto; line-height: 100%; outline: none; text-decoration: none; -ms-interpolation-mode: bicubic; display: inline; border: 0;" width="50" height="50"/></a>
                                <br/>
                                <small>Webpublico©, Gestão Pública</small>'
where tipotemplate = 'SOLICITACAO_ALTERACAO_SENHA';
update templatenfse
set CONTEUDO = CONTEUDO || '                            </td>
                        </tr>
                    </tbody></table>

                    <!--[if (gte mso 9)|(IE)]>
                    </td>
                    </tr>
                    </table>
                    <![endif]-->
                </div>
            </center>
        </td>
    </tr>
</tbody></table>'
where tipotemplate = 'SOLICITACAO_ALTERACAO_SENHA';
