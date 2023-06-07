package br.edu.puc.sorriso24h.infra

class Constants {
    object KEY_SHARED {
        const val EMAIL_LOGIN = "EMAIL_LOGIN"
        const val PASSWORD_LOGIN = "PASSWORD_LOGIN"
        const val SAVE_LOGIN = "SAVE_LOGIN"
        const val SAVED_LOGIN = "SAVED_LOGIN"

        const val NAME_REGISTER = "NAME_REGISTER"
        const val EMAIL_REGISTER = "EMAIL_REGISTER"
        const val PASSWORD_REGISTER = "PASSWORD_REGISTER"
        const val PHONE_NUMBER_REGISTER = "PHONE_NUMBER_REGISTER"
        const val ADDRESS_1_REGISTER = "ADDRESS_1_REGISTER"
        const val ADDRESS_2_REGISTER = "ADDRESS_2_REGISTER"
        const val ADDRESS_3_REGISTER = "ADDRESS_3_REGISTER"
        const val CURRICULUM = "CURRICULUM"

        const val ARRAY_NAME = "ARRAY_NAME"
        const val ARRAY_TEL = "ARRAY_TEL"
        const val ARRAY_ADAPT = "ARRAY_ADAPT"

        const val NOTI = "NOTI"

    }
    object OTHERS {
        const val TRUE = "TRUE"
        const val FALSE = "FALSE"
    }
    object DB {
        const val REGIAO = "southamerica-east1"
        const val ADD_DENTIST_FUNCTION = "addUsers"
        const val DENTISTAS = "Dentistas"
        const val EMERGENCIAS = "Emergencias"
        object FIELD {
            const val CLOSED_STATUS = "statusEncerrada"
            const val NAME_DB = "nome"
            const val EMAIL_DB = "email"
            const val PHONE = "telefone"
            const val UID = "uid"
        }
    }

    object PHRASE {
        const val LIMIT_ADDRESS = "Limite máximo de 3 endereços atingido!"
        const val EMPTY_FIELD = "Campo obrigatório!"
        const val NO_ADDRESS = "Obrigatório no MÍNIMO 1 endereço salvo!"
        const val MIN_LENGHT = "Minimo de 8 digitos!"
        const val MINI_CURR = "Faça um mini curriculo!"

        //Frases tratamento cadastro

        const val PASSWORD_ERROR_REGISTER = "Digite uma nova senha com no minimo 6 digitos!"
        const val INVALID_EMAIL = "Digite um email valido!"
        const val USER_ALREADY_EXISTS = "Esta conta ja existe!"
        const val NO_INTERNET = "Sem conexão com a internet!"
        const val GENERIC_ERROR = "Erro ao cadastrar usuario!"

        //ERRO
        const val ERROR = "ERROR"

        //Frases detalhe activity

        const val EMERGENCY_ACCEPTED = "Voce aceitou essa Emergencia, Aguarde o retorno!"
        const val ALREADY_ACCEPTED_EMERGENCY = "Voce ja aceitou essa emergencia, Aguarde o retorno!"
    }
}