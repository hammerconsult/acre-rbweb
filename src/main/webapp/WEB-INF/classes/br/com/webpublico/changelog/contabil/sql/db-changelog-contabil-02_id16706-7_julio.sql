    DELETE
      SUBFUNCAO
    WHERE
      ID IN
      (
        SELECT
          S.ID
        FROM
          SUBFUNCAO S
        WHERE
          S.FUNCAO_ID IS NULL
      )
