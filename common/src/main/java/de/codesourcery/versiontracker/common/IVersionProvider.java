/**
 * Copyright 2018 Tobias Gierke <tobias.gierke@code-sourcery.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.codesourcery.versiontracker.common;

import java.io.IOException;
import java.util.Set;

/**
 * Responsible for retrieving artifact metadata and applying it to a {@link VersionInfo} instance.
 * 
 * Implementations of this interface <b>have</b> to be thread-safe.
 */
public interface IVersionProvider
{
    enum UpdateResult
    {
        /**
         * No artifact with the given groupID and artifactID could be found.
         */
        ARTIFACT_UNKNOWN,
        /**
         * Artifact was found but the requested version does not exist.
         */
        ARTIFACT_VERSION_NOT_FOUND,
        /**
         * Nothing changed on the server.
         */
        NO_CHANGES_ON_SERVER,
        /**
         * Some fields of the {@link VersionInfo} got updated.
         */
        UPDATED,
        /**
         * No artifact metadata was fetched because the artifact was completely blacklisted.
         * @see IBlacklistCheck#isArtifactBlacklisted(Artifact)
         */
        BLACKLISTED,
        /**
         * Retrieving artifact metadata failed.
         */
        ERROR
    }

    final class Statistics
    {
        public final RequestsPerHour metaDataRequests;
        public final RequestsPerHour releaseDateRequests;

        public Statistics() {
            this.metaDataRequests = new RequestsPerHour();
            this.releaseDateRequests = new RequestsPerHour();
        }

        public Statistics(Statistics other) {
            //noinspection IncompleteCopyConstructor
            this.metaDataRequests = new RequestsPerHour(other.metaDataRequests);
            //noinspection IncompleteCopyConstructor
            this.releaseDateRequests = new RequestsPerHour(other.releaseDateRequests);
        }

        public Statistics createCopy() {
            return new Statistics( this );
        }
    }

    /**
     * Returns usage statistics.
     *
     * @return
     */
    Statistics getStatistics();

    /**
     * Try to update version information.
     * 
     * This method must be <b>thread-safe</b>.
     * 
     * @param info
     * @param additionalVersionsToFetchReleaseDatesFor versions on top of latest snapshot/latest release version for which
     *                                                 the release date should be scraped
     * @return 
     * @throws IOException 
     */
    UpdateResult update(VersionInfo info, Set<String> additionalVersionsToFetchReleaseDatesFor) throws IOException;
}
