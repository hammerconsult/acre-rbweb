ALTER TABLE objetocompraespecificacao RENAME COLUMN texto TO texto_aux;
ALTER TABLE objetocompraespecificacao_aud RENAME COLUMN texto TO texto_aux;

ALTER TABLE objetocompraespecificacao ADD (texto VARCHAR2(3000));
ALTER TABLE objetocompraespecificacao_aud ADD (texto VARCHAR2(3000));

UPDATE objetocompraespecificacao SET texto = TO_CHAR(SUBSTR(texto_aux, 0, 2999));
UPDATE objetocompraespecificacao_aud SET texto = TO_CHAR(SUBSTR(texto_aux, 0, 2999));

ALTER TABLE objetocompraespecificacao DROP COLUMN texto_aux;
ALTER TABLE objetocompraespecificacao_aud DROP COLUMN texto_aux;