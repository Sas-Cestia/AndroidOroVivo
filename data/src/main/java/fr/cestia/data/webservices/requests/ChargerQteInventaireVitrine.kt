package fr.cestia.data.webservices.requests

import fr.cestia.common_files.ConfigurationGenerale.CODE_CLIENT
import fr.cestia.common_files.ConfigurationGenerale.CODE_ENTREPRISE

class ChargerQteInventaireVitrine(private val codeMagasin: String) : BaseRequest() {
    override fun getBody(): String {
        return """
            <tem:ChargerQteInventaireVitrine>
                <tem:codeClient>$CODE_CLIENT</tem:codeClient>
                <tem:codeEntreprise>$CODE_ENTREPRISE</tem:codeEntreprise>
                <tem:codeMagasin>$codeMagasin</tem:codeMagasin>
            </tem:ChargerQteInventaireVitrine>
        """.trimIndent()
    }
}