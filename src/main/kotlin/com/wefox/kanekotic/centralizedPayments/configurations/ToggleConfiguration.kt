import com.wefox.kanekotic.centralizedPayments.configurations.FileConfig
import com.wefox.kanekotic.centralizedPayments.configurations.toggles

object ToggleConfiguration {
    val offline = FileConfig.config[toggles.offline]
    val online = FileConfig.config[toggles.online]
}
