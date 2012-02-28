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

/**
 * This service manage storing and retrieving selections from the database.
 */
class SelectionRepositoryService {

    public static final SCHEME = 'repo' // TODO The 'repo' scheme is hard coded here, is it bad?

    static transactional = true

    /**
     * List selections stored in the repository.
     *
     * The returned List contains Maps of selection properties.
     *     id: Identifier that can be used with the selection(id) method.
     *     name: name of the selection, typically used as menu item labels
     *     description: a text that explains the expected result of using this selection
     *
     * @param location a string that identifies where this selection is valid (i.e. controller or domain name)
     * @param username the user who owns this selection, or null is it's a public selection
     * @param tenant tenant ID or null if not in multi-tenant environment
     * @return a list of selections matching the parameters. Each list entry is a Map with selection properties
     */
    List<Map<String, Object>> list(String location, String username = null, Long tenant = null) {
        def result = SelectionRepository.createCriteria().list([sort: 'name', order: 'asc']) {
            eq('location', location)
            if (username) {
                eq('username', username)
            } else {
                isNull('username')
            }
            if (tenant != null) {
                eq('tenantId', tenant)
            }
        }
        return result.collect {[id: it.id, name: it.name, description: it.description, uri:it.uri.toString()]}
    }

    /**
     * Retrieve a persistent selection
     *
     * @param id primary key of the wanted selection
     * @return the selection's URI
     */
    URI selection(Long id) {
        def s = SelectionRepository.read(id)
        if (!s) {
            throw new IllegalArgumentException("No selection found with id [$id]")
        }
        s.selection
    }

    /**
     * Create or update a persistent selection.
     *
     * @param selection a selection URI or null if only description is to be updated
     * @param tenant optional tenant ID
     * @param location a string that identifies where this selection is valid (i.e. controller or domain name)
     * @param username the user who owns this selection, or null is it's a public selection
     * @param name name of the selection
     * @param description a text that explains the expected result of using this selection
     * @return a selection URI suitable for retrieving/invoking the persisted selection
     */
    URI update(URI selection, Long tenant, String location, String username, String name, String description = null) {
        // Try to find persistent selection.
        def s = SelectionRepository.createCriteria().get() {
            if (tenant != null) {
                eq('tenantId', tenant)
            }
            eq('location', location)
            eq('name', name)
            if (username) {
                eq('username', username)
            } else {
                isNull('username')
            }
        }

        if (!s) {
            // Create new persistent selection
            s = new SelectionRepository(tenantId: tenant, location: location, username: username, name: name)
        }

        // Update URI and description.
        if (description != null) {
            s.description = description ?: null // Empty string will be stored as null.
        }
        if (selection) {
            s.uri = selection.toASCIIString() // Only update if present.
        }

        s.save(failOnError: true)

        return new URI(SCHEME + ':' + s.ident())
    }
}
