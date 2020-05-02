package com.wefox.kanekotic.centralizedPayments.serdes

import com.wefox.kanekotic.centralizedPayments.models.Payment
import com.wefox.kanekotic.centralizedPayments.utils.JsonClassDeserializer
import com.wefox.kanekotic.centralizedPayments.utils.JsonClassSerializer
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.serialization.Serializer
import java.util.HashMap

class PaymentSerde private constructor() {
    val serializer: Serializer<Payment>
    val deserializer: Deserializer<Payment>
    val serde: Serde<Payment>

    init {
        val serdeProps: HashMap<String, Any> = HashMap()
        serializer = JsonClassSerializer()
        serdeProps.put("JsonClassClass", Payment::class.java)
        serializer.configure(serdeProps, false)
        deserializer = JsonClassDeserializer()
        serdeProps.put("JsonClassClass", Payment::class.java)
        deserializer.configure(serdeProps, false)
        serde = Serdes.serdeFrom(serializer, deserializer)
    }

    companion object{
        fun get(): PaymentSerde {
            return PaymentSerde()
        }
    }
}

