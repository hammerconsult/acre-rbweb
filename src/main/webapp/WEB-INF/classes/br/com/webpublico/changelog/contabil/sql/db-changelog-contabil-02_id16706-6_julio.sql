        UPDATE
          DESPESAFECHAMENTOEXERCICIO
        SET
          SUBFUNCAO_ID =
          (
            SELECT
              SF.ID
            FROM
              SUBFUNCAO SF
            WHERE
              SF.CODIGO =
              (
                SELECT
                  SFU.CODIGO
                FROM
                  SUBFUNCAO SFU
                WHERE
                  SFU.ID = SUBFUNCAO_ID
              )
              AND SF.FUNCAO_ID IS NOT NULL
          )
        WHERE
          SUBFUNCAO_ID IN
          (
            SELECT
              S.ID
            FROM
              SUBFUNCAO S
            WHERE
              S.FUNCAO_ID IS NULL
          )
