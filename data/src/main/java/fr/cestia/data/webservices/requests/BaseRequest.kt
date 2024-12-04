package fr.cestia.data.webservices.requests

abstract class BaseRequest {
    // Fonction pour générer l'enveloppe SOAP
    fun createEnvelope(body: String): String {
        return """
            <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                <soapenv:Header/>
                <soapenv:Body>
                    $body
                </soapenv:Body>
            </soapenv:Envelope>
        """.trimIndent()
    }

    // Fonction abstraite pour définir le corps spécifique de chaque requête
    abstract fun getBody(): String

    // Fonction finale pour obtenir la requête complète
    fun buildRequest(): String {
        return createEnvelope(getBody())
    }
}