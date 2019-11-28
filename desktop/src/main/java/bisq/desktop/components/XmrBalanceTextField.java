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

package bisq.desktop.components;

import bisq.core.util.XmrBSFormatter;
import bisq.core.xmr.XmrCoin;

import com.jfoenix.controls.JFXTextField;

import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import javax.annotation.Nullable;

public class XmrBalanceTextField extends AnchorPane {

    private XmrCoin targetAmount;
    private final JFXTextField textField;
    private final Effect fundedEffect = new DropShadow(BlurType.THREE_PASS_BOX, Color.GREEN, 4, 0.0, 0, 0);
    private final Effect notFundedEffect = new DropShadow(BlurType.THREE_PASS_BOX, Color.ORANGERED, 4, 0.0, 0, 0);
    private XmrBSFormatter formatter;
    @Nullable
    private XmrCoin balance;


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Constructor
    ///////////////////////////////////////////////////////////////////////////////////////////

    public XmrBalanceTextField(String label) {
        textField = new BisqTextField();
        textField.setLabelFloat(true);
        textField.setPromptText(label);
        textField.setFocusTraversable(false);
        textField.setEditable(false);
        textField.setId("info-field");

        AnchorPane.setRightAnchor(textField, 0.0);
        AnchorPane.setLeftAnchor(textField, 0.0);

        getChildren().addAll(textField);
    }

    public void setFormatter(XmrBSFormatter formatter) {
        this.formatter = formatter;
    }

    public void setBalance(XmrCoin balance) {
        this.balance = balance;

        updateBalance(balance);
    }

    public void setTargetAmount(XmrCoin targetAmount) {
        this.targetAmount = targetAmount;

        if (this.balance != null)
            updateBalance(balance);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Private methods
    ///////////////////////////////////////////////////////////////////////////////////////////

    private void updateBalance(XmrCoin balance) {
        if (formatter != null)
            textField.setText(formatter.formatCoinWithCode(balance));

        //TODO: replace with new validation logic
//        if (targetAmount != null) {
//            if (balance.compareTo(targetAmount) >= 0)
//                textField.setEffect(fundedEffect);
//            else
//                textField.setEffect(notFundedEffect);
//        } else {
//            textField.setEffect(null);
//        }
    }
}
