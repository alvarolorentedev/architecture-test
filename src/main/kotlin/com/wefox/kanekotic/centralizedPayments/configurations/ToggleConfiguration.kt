import com.wefox.kanekotic.centralizedPayments.configurations.FileConfig

object ToggleConfiguration {
    val offline = FileConfig.config[FileConfig.offlineToggle]
    val online = FileConfig.config[FileConfig.onlineToggle]

}