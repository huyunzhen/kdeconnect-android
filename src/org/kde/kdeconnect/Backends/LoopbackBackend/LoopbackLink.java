/*
 * Copyright 2014 Albert Vaca Cintora <albertvaka@gmail.com>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License or (at your option) version 3 or any later version
 * accepted by the membership of KDE e.V. (or its successor approved
 * by the membership of KDE e.V.), which shall act as a proxy
 * defined in Section 14 of version 3 of the license.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. 
*/

package org.kde.kdeconnect.Backends.LoopbackBackend;

import org.kde.kdeconnect.Backends.BaseLink;
import org.kde.kdeconnect.Backends.BaseLinkProvider;
import org.kde.kdeconnect.Device;
import org.kde.kdeconnect.NetworkPackage;

import java.security.PublicKey;

public class LoopbackLink extends BaseLink {

    public LoopbackLink(BaseLinkProvider linkProvider) {
        super("loopback", linkProvider);
    }

    @Override
    public void sendPackage(NetworkPackage in, Device.SendPackageStatusCallback callback) {
        sendPackageEncrypted(in, callback, null);
    }

    @Override
    public void sendPackageEncrypted(NetworkPackage in, Device.SendPackageStatusCallback callback, PublicKey key) {
        try {
            if (key != null) {
                in = in.encrypt(key);
            }
            String s = in.serialize();
            NetworkPackage out= NetworkPackage.unserialize(s);
            if (key != null) {
                out = out.decrypt(privateKey);
            }
            packageReceived(out);
            if (in.hasPayload()) {
                callback.sendProgress(0);
                out.setPayload(in.getPayload(), in.getPayloadSize());
                callback.sendProgress(100);
            }
            callback.sendSuccess();
        } catch(Exception e) {
            callback.sendFailure(e);
        }
    }
}
