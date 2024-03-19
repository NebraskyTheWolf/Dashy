package eu.fluffici.dashy.ui.activities.modules;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import eu.fluffici.dashy.ui.activities.modules.impl.accounting.AccountingActivity;
import eu.fluffici.dashy.ui.activities.modules.impl.calendar.CalendarActivity;
import eu.fluffici.dashy.ui.activities.modules.impl.logs.AuditActivity;
import eu.fluffici.dashy.ui.activities.modules.impl.orders.OrdersActivity;
import eu.fluffici.dashy.ui.activities.modules.impl.support.SupportActivity;
import eu.fluffici.dashy.ui.activities.modules.impl.users.UsersActivity;

public class ModuleManager {
    private final List<Module> modules = new CopyOnWriteArrayList<>();


    public ModuleManager() {
        this.modules.clear();

        this.modules.add(new UsersActivity());
        this.modules.add(new SupportActivity());
        this.modules.add(new CalendarActivity());
        this.modules.add(new AuditActivity());
        this.modules.add(new OrdersActivity());
        this.modules.add(new AccountingActivity());
    }

    public List<Module> getModules() {
        return this.modules;
    }
}

