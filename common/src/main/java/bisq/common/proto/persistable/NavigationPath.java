/*
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package bisq.common.proto.persistable;

import io.bisq.generated.protobuffer.PB;

import com.google.protobuf.Message;

import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NavigationPath implements PersistableEnvelope {
    private List<String> path = new ArrayList<>();

    @Override
    public Message toProtoMessage() {
        final PB.NavigationPath.Builder builder = PB.NavigationPath.newBuilder();
        if (!CollectionUtils.isEmpty(path)) builder.addAllPath(path);
        return PB.PersistableEnvelope.newBuilder().setNavigationPath(builder).build();
    }

    public static PersistableEnvelope fromProto(PB.NavigationPath proto) {
        return new NavigationPath(new ArrayList<>(proto.getPathList()));
    }
}