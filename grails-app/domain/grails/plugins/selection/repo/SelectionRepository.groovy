/*
 *  Copyright 2012 Goran Ehrsson.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package grails.plugins.selection.repo

class SelectionRepository {

    Long tenantId
    String location
    String username
    String name
    String description
    String uri

    static constraints = {
        tenantId(nullable:true)
        location(maxSize:255, blank:false)
        username(maxSize:80, nullable:true)
        name(maxSize:80, blank:false, unique:['username', 'location', 'tenantId'])
        description(maxSize:2000, nullable:true)
        uri(maxSize:2000, blank:false)
    }

    static transients = ["selection"]

    URI getSelection() {
        new URI(uri)
    }

    String toString() {
        name
    }
}
