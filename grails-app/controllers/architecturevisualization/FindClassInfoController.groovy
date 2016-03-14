package architecturevisualization

import grails.converters.JSON
import main.CreateBasedPlantUMLFile
import main.DiscoverClassInfo

class FindClassInfoController {

    def index() {
		def dci = new DiscoverClassInfo()
		dci.findClassInfo()
		render view: "index", model: [map : dci.createBasedPlantUMLFile.map as JSON]
	}

}