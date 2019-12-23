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

package bisq.desktop.main.offer.xmr;

import bisq.desktop.Navigation;
import bisq.desktop.common.view.ActivatableView;
import bisq.desktop.common.view.View;
import bisq.desktop.common.view.ViewLoader;
import bisq.desktop.main.MainView;
import bisq.desktop.main.offer.xmr.createoffer.XmrCreateOfferView;
import bisq.desktop.main.offer.xmr.offerbook.XmrOfferBookView;
import bisq.desktop.main.offer.xmr.takeoffer.XmrTakeOfferView;
import bisq.desktop.main.overlays.popups.Popup;
import bisq.desktop.util.GUIUtil;

import bisq.core.locale.CurrencyUtil;
import bisq.core.locale.GlobalSettings;
import bisq.core.locale.LanguageUtil;
import bisq.core.locale.Res;
import bisq.core.locale.TradeCurrency;
import bisq.core.offer.Offer;
import bisq.core.offer.OfferPayload;
import bisq.core.support.dispute.arbitration.arbitrator.ArbitratorManager;
import bisq.core.user.Preferences;
import bisq.core.user.User;

import bisq.network.p2p.P2PService;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

import javafx.beans.value.ChangeListener;

import javafx.collections.ListChangeListener;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static bisq.desktop.main.offer.xmr.XmrMutableOfferView.BUYER_SECURITY_DEPOSIT_NEWS;

public abstract class XmrOfferView extends ActivatableView<TabPane, Void> {

    private XmrOfferBookView offerBookView;
    private XmrCreateOfferView createOfferView;
    private XmrTakeOfferView takeOfferView;
    private AnchorPane createOfferPane, takeOfferPane;
    private Tab takeOfferTab, createOfferTab, offerBookTab;

    private final ViewLoader viewLoader;
    private final Navigation navigation;
    private final Preferences preferences;
    private final User user;
    private final P2PService p2PService;
    private final OfferPayload.Direction direction;
    private final ArbitratorManager arbitratorManager;

    private Offer offer;
    private TradeCurrency tradeCurrency;
    private boolean createOfferViewOpen, takeOfferViewOpen;
    private Navigation.Listener navigationListener;
    private ChangeListener<Tab> tabChangeListener;
    private ListChangeListener<Tab> tabListChangeListener;

    protected XmrOfferView(ViewLoader viewLoader,
                        Navigation navigation,
                        Preferences preferences,
                        ArbitratorManager arbitratorManager,
                        User user,
                        P2PService p2PService) {
        this.viewLoader = viewLoader;
        this.navigation = navigation;
        this.preferences = preferences;
        this.user = user;
        this.p2PService = p2PService;
        this.direction = (this instanceof XmrBuyOfferView) ? OfferPayload.Direction.BUY : OfferPayload.Direction.SELL;
        this.arbitratorManager = arbitratorManager;
    }

    @Override
    protected void initialize() {
        navigationListener = viewPath -> {
            if (viewPath.size() == 4 && viewPath.indexOf(this.getClass()) == 2)
                loadView(viewPath.tip());
        };
        tabChangeListener = (observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.equals(createOfferTab) && createOfferView != null) {
                    createOfferView.onTabSelected(true);
                } else if (newValue.equals(takeOfferTab) && takeOfferView != null) {
                    takeOfferView.onTabSelected(true);
                } else if (newValue.equals(offerBookTab) && offerBookView != null) {
                    offerBookView.onTabSelected(true);
                }
            }
            if (oldValue != null) {
                if (oldValue.equals(createOfferTab) && createOfferView != null) {
                    createOfferView.onTabSelected(false);
                } else if (oldValue.equals(takeOfferTab) && takeOfferView != null) {
                    takeOfferView.onTabSelected(false);
                } else if (oldValue.equals(offerBookTab) && offerBookView != null) {
                    offerBookView.onTabSelected(false);
                }
            }
        };
        tabListChangeListener = change -> {
            change.next();
            List<? extends Tab> removedTabs = change.getRemoved();
            if (removedTabs.size() == 1) {
                if (removedTabs.get(0).getContent().equals(createOfferPane))
                    onCreateOfferViewRemoved();
                else if (removedTabs.get(0).getContent().equals(takeOfferPane))
                    onTakeOfferViewRemoved();
            }
        };
    }

    @Override
    protected void activate() {
        Optional<TradeCurrency> tradeCurrencyOptional = (this instanceof XmrSellOfferView) ?
                CurrencyUtil.getTradeCurrency(preferences.getSellScreenCurrencyCode()) :
                CurrencyUtil.getTradeCurrency(preferences.getBuyScreenCurrencyCode());
        tradeCurrency = tradeCurrencyOptional.orElseGet(GlobalSettings::getDefaultTradeCurrency);

        root.getSelectionModel().selectedItemProperty().addListener(tabChangeListener);
        root.getTabs().addListener(tabListChangeListener);
        navigation.addListener(navigationListener);
        navigation.navigateTo(MainView.class, XmrTradeBaseView.class, this.getClass(), XmrOfferBookView.class);
    }

    @Override
    protected void deactivate() {
        navigation.removeListener(navigationListener);
        root.getSelectionModel().selectedItemProperty().removeListener(tabChangeListener);
        root.getTabs().removeListener(tabListChangeListener);
    }

    private String getCreateOfferTabName() {
        return Res.get("offerbook.createOffer").toUpperCase();
    }

    private String getTakeOfferTabName() {
        return Res.get("offerbook.takeOffer").toUpperCase();
    }

    private void loadView(Class<? extends View> viewClass) {
        TabPane tabPane = root;
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        View view;
        boolean isBuy = direction == OfferPayload.Direction.BUY;

        if (viewClass == XmrOfferBookView.class && offerBookView == null) {
            view = viewLoader.load(viewClass);
            // Offerbook must not be cached by ViewLoader as we use 2 instances for sell and buy screens.
//            offerBookTab = new Tab(isBuy ? Res.get("shared.buyXxx").toUpperCase() : Res.get("shared.sellXxx").toUpperCase());
            offerBookTab = new Tab(Res.get("offerbook.availableOffers").toUpperCase());
            offerBookTab.setClosable(false);
            offerBookTab.setContent(view.getRoot());
            tabPane.getTabs().add(offerBookTab);
            offerBookView = (XmrOfferBookView) view;
            offerBookView.onTabSelected(true);

            OfferActionHandler offerActionHandler = new OfferActionHandler() {
                @Override
                public void onCreateOffer(TradeCurrency tradeCurrency) {
                    if (!createOfferViewOpen) {
                        if (canCreateOrTakeOffer()) {
                            openCreateOffer(tradeCurrency);
                        }
                    } else {
                        log.error("You have already a \"Create offer\" tab open.");
                    }
                }

                @Override
                public void onTakeOffer(Offer offer) {
                    if (!takeOfferViewOpen) {
                        if (canCreateOrTakeOffer()) {
                            openTakeOffer(offer);
                        }
                    } else {
                        log.error("You have already a \"Take offer\" tab open.");
                    }
                }
            };
            offerBookView.setOfferActionHandler(offerActionHandler);
            offerBookView.setDirection(direction);
        } else if (viewClass == XmrCreateOfferView.class && createOfferView == null) {
            view = viewLoader.load(viewClass);
            // CreateOffer and TakeOffer must not be cached by ViewLoader as we cannot use a view multiple times
            // in different graphs
            createOfferView = (XmrCreateOfferView) view;
            createOfferView.initWithData(direction, tradeCurrency);
            createOfferPane = createOfferView.getRoot();
            createOfferTab = new Tab(getCreateOfferTabName());
            createOfferTab.setClosable(true);
            // close handler from close on create offer action
            createOfferView.setCloseHandler(() -> tabPane.getTabs().remove(createOfferTab));
            createOfferTab.setContent(createOfferPane);
            tabPane.getTabs().add(createOfferTab);
            tabPane.getSelectionModel().select(createOfferTab);
        } else if (viewClass == XmrTakeOfferView.class && takeOfferView == null && offer != null) {
            view = viewLoader.load(viewClass);
            // CreateOffer and TakeOffer must not be cached by ViewLoader as we cannot use a view multiple times
            // in different graphs
            takeOfferView = (XmrTakeOfferView) view;
            takeOfferView.initWithData(offer);
            takeOfferPane = ((XmrTakeOfferView) view).getRoot();
            takeOfferTab = new Tab(getTakeOfferTabName());
            takeOfferTab.setClosable(true);
            // close handler from close on take offer action
            takeOfferView.setCloseHandler(() -> tabPane.getTabs().remove(takeOfferTab));
            takeOfferTab.setContent(takeOfferPane);
            tabPane.getTabs().add(takeOfferTab);
            tabPane.getSelectionModel().select(takeOfferTab);
        }
    }

    protected boolean canCreateOrTakeOffer() {
        return GUIUtil.isBootstrappedOrShowPopup(p2PService) &&
                GUIUtil.canCreateOrTakeOfferOrShowPopup(user, navigation);
    }

    private void showNoArbitratorForUserLocaleWarning() {
        String key = "NoArbitratorForUserLocaleWarning";
        new Popup<>().information(Res.get("offerbook.info.noArbitrationInUserLanguage",
                getArbitrationLanguages(), LanguageUtil.getDisplayName(preferences.getUserLanguage())))
                .closeButtonText(Res.get("shared.ok"))
                .dontShowAgainId(key)
                .show();
    }

    private String getArbitrationLanguages() {
        return arbitratorManager.getObservableMap().values().stream()
                .flatMap(arbitrator -> arbitrator.getLanguageCodes().stream())
                .distinct()
                .map(languageCode -> LanguageUtil.getDisplayName(languageCode))
                .collect(Collectors.joining(", "));
    }

    private void openTakeOffer(Offer offer) {
        XmrOfferView.this.takeOfferViewOpen = true;
        XmrOfferView.this.offer = offer;
        XmrOfferView.this.navigation.navigateTo(MainView.class, XmrTradeBaseView.class, XmrOfferView.this.getClass(), XmrTakeOfferView.class);
    }

    private void openCreateOffer(TradeCurrency tradeCurrency) {
        XmrOfferView.this.createOfferViewOpen = true;
        XmrOfferView.this.tradeCurrency = tradeCurrency;
        XmrOfferView.this.navigation.navigateTo(MainView.class, XmrTradeBaseView.class, XmrOfferView.this.getClass(), XmrCreateOfferView.class);
    }

    private void onCreateOfferViewRemoved() {
        createOfferViewOpen = false;
        if (createOfferView != null) {
            createOfferView.onClose();
            createOfferView = null;
        }
        offerBookView.enableCreateOfferButton();

        navigation.navigateTo(MainView.class, XmrTradeBaseView.class, this.getClass(), XmrOfferBookView.class);

        preferences.dontShowAgain(BUYER_SECURITY_DEPOSIT_NEWS, true);
    }

    private void onTakeOfferViewRemoved() {
        offer = null;
        takeOfferViewOpen = false;
        if (takeOfferView != null) {
            takeOfferView.onClose();
            takeOfferView = null;
        }

        navigation.navigateTo(MainView.class, XmrTradeBaseView.class, this.getClass(), XmrOfferBookView.class);
    }

    public interface OfferActionHandler {
        void onCreateOffer(TradeCurrency tradeCurrency);

        void onTakeOffer(Offer offer);
    }

    public interface CloseHandler {
        void close();
    }
}