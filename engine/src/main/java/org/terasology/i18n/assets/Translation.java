/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasology.i18n.assets;

import java.util.Locale;
import java.util.Map;

import org.terasology.assets.Asset;
import org.terasology.assets.AssetType;
import org.terasology.assets.ResourceUrn;
import org.terasology.engine.Uri;

/**
 * Defines a translation asset.
 */
public abstract class Translation extends Asset<TranslationData> {

    protected Translation(ResourceUrn urn, AssetType<?, TranslationData> assetType) {
        super(urn, assetType);
    }

    /**
     * @return the uri of the project this instance is part of
     */
    public abstract Uri getProjectUri();

    /**
     * @return the locale of the translation data
     */
    public abstract Locale getLocale();

    /**
     * @return the translation data, possibly unmodifiable
     */
    public abstract Map<String, String> getTranslations();
}