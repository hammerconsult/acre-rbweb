delete
from templatenfse
where TIPOTEMPLATE = 'EMAIL_NOTAFISCAL';
insert into templatenfse (id, tipotemplate, conteudo)
values (hibernate_sequence.nextval, 'EMAIL_NOTAFISCAL',
        '<table class="email-canvas  " style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0; border-collapse: collapse; margin: 0 auto;" width="100%" height="100%" cellspacing="0" cellpadding="0" border="0" bgcolor="#f0f0f0">
            <tbody><tr style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%;">
                <td style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; mso-table-lspace: 0pt; mso-table-rspace: 0pt;" valign="top">
                    <center style="width: 100%; -ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%;">

                        <!--
                            Set the email width. Defined in two places:
                            1. max-width for all clients except Desktop Windows Outlook, allowing the email to squish on narrow but never go wider than 600px.
                            2. MSO tags for Desktop Windows Outlook enforce a 600px width.
                        -->
        ');
update templatenfse
set CONTEUDO = CONTEUDO || '
                <div class="email-wrapper" style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; max-width: 600px; margin: auto;">
                    <!--[if (gte mso 9)|(IE)]>
                    <table cellspacing="0" cellpadding="0" border="0" width="600" align="center">
                        <tr>
                            <td>
                    <![endif]-->

                    <table class="email-header" style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0; border-collapse: collapse; max-width: 600px; margin: 0 auto;" width="100%" cellspacing="0" cellpadding="0" border="0" align="center">
                        <tbody><tr style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%;">
                            <td style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-family: &quot;Helvetica Neue&quot;, sans-serif; color: #999; font-size: 13px; line-height: 1.6; padding: 20px 0;" align="center">
                                <a href="$LINK" style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; color: #757575; text-decoration: none;"><img id="headerImage" alt="NFS-e" src="http://nota.riobranco.ac.gov.br/assets/images/servicos-online.png" style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; height: auto; line-height: 100%; outline: none; text-decoration: none; -ms-interpolation-mode: bicubic; max-width: 600px !important; border: 0;" width="150"/></a>
                            </td>
                        </tr>
                    </tbody></table>
'
where tipotemplate = 'EMAIL_NOTAFISCAL';
update templatenfse
set CONTEUDO = CONTEUDO || '
                    <table class="email-body" style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0; border-collapse: collapse; max-width: 600px; margin: 0 auto;" width="100%" cellspacing="0" cellpadding="0" border="0" bgcolor="#ffffff" align="center">
                        <tbody><tr style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%;">
                            <td style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; mso-table-lspace: 0pt; mso-table-rspace: 0pt;">
                                <table style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0; border-collapse: collapse; margin: 0 auto;" width="100%" cellspacing="0" cellpadding="30" border="0">
                                    <tbody><tr style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%;">
                                        <td class="email-body-content" style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-family: &quot;Helvetica Neue&quot;, sans-serif; color: #444; font-size: 14px; line-height: 150%;" valign="top">
'
where tipotemplate = 'EMAIL_NOTAFISCAL';
update templatenfse
set CONTEUDO = CONTEUDO || '
                                            <div id="greeting" style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; margin-bottom: 20px; padding-bottom: 25px; border-bottom-width: 1px; border-bottom-color: #eee; border-bottom-style: solid;">
                                                <table style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0; border-collapse: collapse; margin: 0 auto;" width="100%" cellspacing="0" cellpadding="0" border="0">
                                                    <tbody><tr style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%;">
                                                        <td style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; mso-table-lspace: 0pt; mso-table-rspace: 0pt;">
                                                            <p>Esta
                                                                mensagem refere-se a<span class="Apple-converted-space">&nbsp;</span><strong><span class="il">Nota</span><span class="Apple-converted-space">&nbsp;</span><span class="il">Fiscal</span><span class="Apple-converted-space">&nbsp;</span>de
                                                                    Serviços
                                                                    Eletrônica - NFS-e Nº. $NUMERO,</strong><br/>
                                                                Emitida
                                                                pelo
                                                                prestador de serviços:</p>
                                                        </td>
                                                        <td class="user-avatar" style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; mso-table-lspace: 0pt; mso-table-rspace: 0pt;" width="58" align="right">
                                                        <br/></td>
                                                    </tr>
                                                </tbody></table>
                                            </div>'
where tipotemplate = 'EMAIL_NOTAFISCAL';
update templatenfse
set CONTEUDO = CONTEUDO || '
                                            <table style="color: rgb(34, 34, 34); font-family: arial,sans-serif; font-size: 12.8px; font-style: normal; font-variant: normal; font-weight: normal; letter-spacing: normal; line-height: normal; text-align: start; text-indent: 0px; text-transform: none; white-space: normal; widows: 1; word-spacing: 0px; width: 617px; background-color: rgb(255, 255, 255); height: 376px;" cellspacing="0" cellpadding="0" border="0" align="left">
                                                <tbody>
                                                <tr>
                                                    <td style="font-family: ''Helvetica Neue'', sans-serif; margin: 0px;">
                                                        <table style="width: 500px;" cellspacing="0" cellpadding="0" border="0" align="left">
                                                            <tbody>
                                                            <tr>
                                                                <td style="font-family: ''Helvetica Neue'', sans-serif; margin: 0px;">
                                                                    Nome/Razão Social:
                                                                </td>
                                                                <td style="font-family: ''Helvetica Neue'', sans-serif; margin: 0px;">
                                                                    $RAZAO_SOCIAL_PRESTADOR
                                                                </td>
                                                            </tr>'
where tipotemplate = 'EMAIL_NOTAFISCAL';
update templatenfse
set CONTEUDO = CONTEUDO || '
                                                            <tr>
                                                                <td style="font-family: ''Helvetica Neue'', sans-serif; margin: 0px;">
                                                                    E-mail:
                                                                </td>
                                                                <td style="font-family: ''Helvetica Neue'', sans-serif; margin: 0px;">
                                                                    <span style="color: rgb(0, 0, 0);">$EMAIL_PRESTADOR</span>
                                                                </td>
                                                            </tr>
                                                            <tr>
                                                                <td style="font-family: ''Helvetica Neue'', sans-serif; margin: 0px;">
                                                                    CPF/CNPJ:
                                                                </td>
                                                                <td style="font-family: ''Helvetica Neue'', sans-serif; margin: 0px;">
                                                                    $CPF_CNPJ_PRESTADOR<br/></td>
                                                            </tr>
                                                            </tbody>
                                                        </table>
                                                    </td>
                                                </tr>'
where tipotemplate = 'EMAIL_NOTAFISCAL';
update templatenfse
set CONTEUDO = CONTEUDO ||
               '
              <tr>
                  <td style="font-family: ''Helvetica Neue'', sans-serif; margin: 0px;">
                      <br/><br/>Para
                      visualizá-la acesse o link:<br/><span style="color: rgb(17, 85, 204);">$LINK_NOTA</span><br/><br/><br/>
                  </td>
              </tr>
              <tr>
                  <td style="font-family: ''Helvetica Neue'', sans-serif; margin: 0px;">
                      Ou se
                      preferir, acesse<span class="Apple-converted-space"> </span><span style="color: rgb(17, 85, 204);">$LINK_SISTEMA</span><span class="Apple-converted-space"> </span>e verifique a
                      autenticidade desta NFS-e informando os dados a seguir:<br/>
                      <table style="width: 500px;" cellspacing="0" cellpadding="0" border="0" align="left">
'
where tipotemplate = 'EMAIL_NOTAFISCAL';
update templatenfse
set CONTEUDO = CONTEUDO || ' <tbody>
                                                            <tr>
                                                                <td style="font-family: ''Helvetica Neue'', sans-serif; margin: 0px;">
                                                                    CNPJ do Prestador:
                                                                </td>
                                                                <td style="font-family: ''Helvetica Neue'', sans-serif; margin: 0px;">
                                                                    <strong>$CPF_CNPJ_PRESTADOR</strong></td>
                                                            </tr>
                                                            <tr>
                                                                <td style="font-family: ''Helvetica Neue'', sans-serif; margin: 0px;">
                                                                    Número da NFS-e:
                                                                </td>
                                                                <td style="font-family: ''Helvetica Neue'', sans-serif; margin: 0px;">
                                                                    <strong>$NUMERO</strong></td>
                                                            </tr>
                                                            <tr>
                                                                <td style="font-family: ''Helvetica Neue'', sans-serif; margin: 0px;">
                                                                    Código Validador:
                                                                </td>
                                                                <td style="font-family: ''Helvetica Neue'', sans-serif; margin: 0px;">
                                                                    <strong>$CODIGO</strong></td>
                                                            </tr>'
where tipotemplate = 'EMAIL_NOTAFISCAL';
update templatenfse
set CONTEUDO = CONTEUDO ||
               '
              </tbody>
          </table>
      </td>
  </tr>
  <tr>
      <td style="font-family: ''Helvetica Neue'', sans-serif; margin: 0px;">
          <p><br/>
          </p>
          <p><br/>Este e-mail foi enviado automaticamente pelo Sistema
              de<span class="Apple-converted-space">&nbsp;</span><span class="il">Nota</span><span class="Apple-converted-space">&nbsp;</span><span class="il">Fiscal</span><span class="Apple-converted-space">&nbsp;</span>de
              Serviços, favor não responder.<br/>Em caso de dúvidas, entre
              em contato no portal<span class="Apple-converted-space">&nbsp;</span><span style="color: rgb(17, 85, 204);">$LINK_SISTEMA</span>
          </p></td>
  </tr>
  </tbody>
</table>
<p><br/></p>
'
where tipotemplate = 'EMAIL_NOTAFISCAL';
update templatenfse
set CONTEUDO = CONTEUDO || '

                                            <p style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; margin-top: 30px; margin-bottom: 15px; font-size: 12px; line-height: 1.5; color: #555;">
                                                Mensagem enviada automaticamente. Por favor, não responda. <br style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%;"/>
                                                Atenciosamente,<br style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%;"/>
                                                Departamento de Tributação</p>


                                        </td>
                                    </tr>
                                </tbody></table>


                            </td>
                        </tr>
                    </tbody></table>

                    <table class="email-footer" style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0; border-collapse: collapse; max-width: 600px; margin: 0 auto;" width="100%" cellspacing="0" cellpadding="0" border="0" align="center">
                        <tbody><tr style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%;">
                            <td style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-family: &quot;Helvetica Neue&quot;, sans-serif; color: #999; font-size: 13px; line-height: 1.6; padding: 30px 5%;" align="center">
                                <div class="address" style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; margin-top: 10px;">


                                    <p>Prefeitura de Rio Branco - Horário de Atendimento: 08h às 18h</p>
                                    <p>Rua Rui Barbosa, 285 – Centro - Rio Branco/AC - CEP: 69.900-901 - Tel.: (68)
                                        3212-7040</p>
'
where tipotemplate = 'EMAIL_NOTAFISCAL';
update templatenfse
set CONTEUDO = CONTEUDO || '

                                </div>
                                <br style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%;"/><br style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%;"/>
                                <a href="http://nota.riobranco.ac.gov.br" style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; color: #757575;"><img alt="Dribbble" src="https://nfsetributech.wphomologacao.com.br/assets/images/logo-wp-62.png" style="-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; height: auto; line-height: 100%; outline: none; text-decoration: none; -ms-interpolation-mode: bicubic; display: inline; border: 0;" width="50" height="50"/></a>
                                <br/>
                                <small>Webpublico©, Gestão Pública</small>
                            </td>
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
where tipotemplate = 'EMAIL_NOTAFISCAL';

