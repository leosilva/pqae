class UrlMappings {

	static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(controller:"index", action: "index")
        "500"(controller: 'error' , action: 'error505')
        "404"(controller: 'error' , action: 'error404')
	}
}
