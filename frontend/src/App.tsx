import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { 
  ShoppingBag, 
  User, 
  Utensils, 
  DollarSign, 
  ListFilter, 
  Activity, 
  AlertTriangle, 
  CheckCircle2, 
  Clock, 
  Truck, 
  ChefHat, 
  CreditCard,
  RefreshCw,
  XCircle
} from 'lucide-react';

interface Order {
  id: number;
  customerName: string;
  item: string;
  amount: number;
  status: string;
}

function App() {
  // Form State
  const [customerName, setCustomerName] = useState('');
  const [item, setItem] = useState('Gourmet Burger');
  const [amount, setAmount] = useState('15.99');
  const [formSubmitting, setFormSubmitting] = useState(false);
  const [formSuccess, setFormSuccess] = useState<string | null>(null);
  const [formError, setFormError] = useState<string | null>(null);

  // Dashboard Data State
  const [orders, setOrders] = useState<Order[]>([]);
  const [loading, setLoading] = useState(true);
  const [unreachable, setUnreachable] = useState(false);
  const [lastRefreshed, setLastRefreshed] = useState<Date>(new Date());

  // Available Menu Items for convenience
  const menuItems = [
    { name: 'Gourmet Burger', price: '15.99' },
    { name: 'Truffle Mushroom Pizza', price: '22.50' },
    { name: 'Fresh Avocado Salad', price: '12.00' },
    { name: 'Premium Spicy Ramen', price: '18.75' },
    { name: 'Fabulous Sushi Combo', price: '34.99' },
    { name: 'Special Spicy Seafood (Simulate Payment Failure)', price: '1050.00' }
  ];

  // Fetch orders from backend API
  const fetchOrders = async (showLoading = false) => {
    if (showLoading) setLoading(true);
    try {
      const response = await axios.get<Order[]>('/api/orders');
      setOrders(response.data.reverse()); // Show newest first
      setUnreachable(false);
      setLastRefreshed(new Date());
    } catch (error) {
      console.error('Error fetching orders:', error);
      setUnreachable(true);
    } finally {
      if (showLoading) setLoading(false);
    }
  };

  // Poll backend every 2 seconds
  useEffect(() => {
    fetchOrders(true);
    const interval = setInterval(() => {
      fetchOrders(false);
    }, 2000);

    return () => clearInterval(interval);
  }, []);

  // Handle item change to automatically adjust amount
  const handleItemChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const selectedItem = e.target.value;
    setItem(selectedItem);
    const found = menuItems.find(m => m.name === selectedItem);
    if (found) {
      setAmount(found.price);
    }
  };

  // Submit Order Form
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!customerName.trim()) {
      setFormError('Customer Name is required');
      return;
    }
    
    setFormSubmitting(true);
    setFormError(null);
    setFormSuccess(null);

    try {
      const response = await axios.post('/api/orders', {
        customerName: customerName.trim(),
        item: item,
        amount: parseFloat(amount)
      });
      
      setFormSuccess(`Order #${response.data.id} placed successfully!`);
      setCustomerName('');
      fetchOrders(false);
    } catch (error) {
      console.error('Error creating order:', error);
      setFormError('Failed to place order. Backend might be offline.');
    } finally {
      setFormSubmitting(false);
    }
  };

  // Helper to render Status Icon
  const getStatusIcon = (status: string) => {
    switch (status.toUpperCase()) {
      case 'PLACED':
        return <Clock className="w-5 h-5 text-sky-400" />;
      case 'PENDING':
        return <Clock className="w-5 h-5 text-rose-400" />;
      case 'PAID':
        return <CreditCard className="w-5 h-5 text-purple-400" />;
      case 'PREPARING':
        return <ChefHat className="w-5 h-5 text-yellow-400" />;
      case 'READY':
        return <ChefHat className="w-5 h-5 text-emerald-400" />;
      case 'OUT_FOR_DELIVERY':
        return <Truck className="w-5 h-5 text-orange-400" />;
      case 'DELIVERED':
        return <CheckCircle2 className="w-5 h-5 text-green-400" />;
      case 'CANCELLED':
        return <XCircle className="w-5 h-5 text-red-500" />;
      default:
        return <Activity className="w-5 h-5 text-slate-400" />;
    }
  };

  // Helper to get status color variable name
  const getStatusColor = (status: string) => {
    switch (status.toUpperCase()) {
      case 'PLACED': return 'var(--status-placed)';
      case 'PENDING': return 'var(--status-pending)';
      case 'PAID': return 'var(--status-paid)';
      case 'PREPARING': return 'var(--status-preparing)';
      case 'READY': return 'var(--status-ready)';
      case 'OUT_FOR_DELIVERY': return 'var(--status-delivery)';
      case 'DELIVERED': return 'var(--status-delivered)';
      case 'CANCELLED': return 'var(--status-cancelled)';
      default: return 'var(--text-secondary)';
    }
  };

  // Helper to map active step index (0 to 4)
  const getStepProgressIndex = (status: string) => {
    const s = status.toUpperCase();
    if (s === 'CANCELLED') return -1;
    if (s === 'PLACED' || s === 'PENDING') return 0;
    if (s === 'PAID') return 1;
    if (s === 'PREPARING' || s === 'READY') return 2;
    if (s === 'OUT_FOR_DELIVERY') return 3;
    if (s === 'DELIVERED') return 4;
    return 0;
  };

  return (
    <div style={containerStyle}>
      {/* Header Banner */}
      <header style={headerStyle}>
        <div>
          <h1 style={brandTitleStyle}>
            <ShoppingBag className="w-9 h-9 inline-block mr-2 text-rose-500 align-text-bottom" />
            FOODIE<span style={{ color: 'var(--accent-color)' }}>DASH</span>
          </h1>
          <p style={brandDescStyle}>Online Food Ordering Orchestration System</p>
        </div>
        
        {/* Connection status indicator */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
          {unreachable ? (
            <span style={errorBadgeStyle}>
              <AlertTriangle className="w-4 h-4 mr-1 inline-block pulse" />
              Backend Unreachable
            </span>
          ) : (
            <span style={successBadgeStyle}>
              <Activity className="w-4 h-4 mr-1 inline-block" />
              Connected
            </span>
          )}
          <button onClick={() => fetchOrders(true)} style={iconButtonStyle} title="Refresh manually">
            <RefreshCw className={`w-4 h-4 ${loading ? 'spinner' : ''}`} />
          </button>
        </div>
      </header>

      {/* Main Grid */}
      <div style={gridStyle}>
        
        {/* Left Column: Form Card */}
        <section style={cardStyle}>
          <div style={cardHeaderStyle}>
            <ShoppingBag className="w-6 h-6 text-rose-500" />
            <h2 style={cardTitleStyle}>Place New Order</h2>
          </div>
          
          <form onSubmit={handleSubmit} style={formStyle}>
            {formSuccess && <div style={formSuccessMessageStyle}>{formSuccess}</div>}
            {formError && <div style={formErrorMessageStyle}>{formError}</div>}

            <div style={inputGroupStyle}>
              <label style={labelStyle}>
                <User className="w-4 h-4 inline mr-1 text-slate-400" />
                Customer Name
              </label>
              <input
                type="text"
                placeholder="Enter customer name"
                value={customerName}
                onChange={(e) => setCustomerName(e.target.value)}
                style={inputStyle}
                disabled={formSubmitting}
              />
            </div>

            <div style={inputGroupStyle}>
              <label style={labelStyle}>
                <Utensils className="w-4 h-4 inline mr-1 text-slate-400" />
                Choose Item
              </label>
              <select
                value={item}
                onChange={handleItemChange}
                style={selectStyle}
                disabled={formSubmitting}
              >
                {menuItems.map((m, idx) => (
                  <option key={idx} value={m.name} style={{ color: '#0f172a' }}>
                    {m.name} (${m.price})
                  </option>
                ))}
              </select>
            </div>

            <div style={inputGroupStyle}>
              <label style={labelStyle}>
                <DollarSign className="w-4 h-4 inline mr-1 text-slate-400" />
                Total Amount ($)
              </label>
              <input
                type="text"
                value={amount}
                readOnly
                style={readOnlyInputStyle}
              />
              <small style={{ color: 'var(--text-secondary)', marginTop: '0.25rem', display: 'block' }}>
                Note: Orders exceeding $1000 will simulate payment failure.
              </small>
            </div>

            <button
              type="submit"
              style={formSubmitting ? disabledButtonStyle : submitButtonStyle}
              disabled={formSubmitting}
            >
              {formSubmitting ? 'Submitting Order...' : 'Submit Order'}
            </button>
          </form>
        </section>

        {/* Right Column: Dashboard Card */}
        <section style={cardStyle}>
          <div style={cardHeaderStyle}>
            <ListFilter className="w-6 h-6 text-rose-500" />
            <h2 style={cardTitleStyle}>Real-Time Tracking System</h2>
            <span style={timeBadgeStyle}>
              Last Polled: {lastRefreshed.toLocaleTimeString()}
            </span>
          </div>

          {loading && orders.length === 0 ? (
            <div style={loaderContainerStyle}>
              <RefreshCw className="w-8 h-8 spinner text-rose-500" />
              <p style={{ marginTop: '0.75rem', color: 'var(--text-secondary)' }}>Loading orders...</p>
            </div>
          ) : orders.length === 0 ? (
            <div style={emptyContainerStyle}>
              <ShoppingBag className="w-12 h-12 text-slate-600 mb-2" />
              <p style={{ color: 'var(--text-secondary)' }}>No orders placed yet.</p>
              <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', marginTop: '0.25rem' }}>
                Use the form on the left to submit a transaction.
              </p>
            </div>
          ) : (
            <div style={ordersContainerStyle}>
              {orders.map((o) => {
                const stepIndex = getStepProgressIndex(o.status);
                const isCancelled = o.status.toUpperCase() === 'CANCELLED';
                
                return (
                  <div key={o.id} style={orderRowStyle}>
                    {/* Header info */}
                    <div style={orderRowHeaderStyle}>
                      <div>
                        <span style={orderIdBadgeStyle}>Order #{o.id}</span>
                        <strong style={{ marginLeft: '0.75rem', fontSize: '1.05rem' }}>{o.customerName}</strong>
                      </div>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                        <span style={{ fontSize: '1.05rem', fontWeight: 600 }}>${o.amount.toFixed(2)}</span>
                        <span style={{
                          backgroundColor: getStatusColor(o.status) + '1c',
                          color: getStatusColor(o.status),
                          borderColor: getStatusColor(o.status) + '4d',
                          borderWidth: '1px',
                          borderStyle: 'solid',
                          padding: '0.25rem 0.5rem',
                          borderRadius: '0.25rem',
                          fontSize: '0.8rem',
                          fontWeight: 700,
                          display: 'flex',
                          alignItems: 'center',
                          gap: '0.25rem'
                        }}>
                          {getStatusIcon(o.status)}
                          {o.status}
                        </span>
                      </div>
                    </div>

                    {/* Item ordered */}
                    <p style={{ fontSize: '0.95rem', color: 'var(--text-secondary)', margin: '0.5rem 0 1rem 0' }}>
                      Item: <span style={{ color: 'var(--text-primary)', fontWeight: 500 }}>{o.item}</span>
                    </p>

                    {/* Steps Progress Indicator */}
                    {!isCancelled ? (
                      <div style={progressTrackerStyle}>
                        {['Placed', 'Payment', 'Kitchen', 'Delivery', 'Delivered'].map((step, idx) => {
                          const isActive = idx <= stepIndex;
                          return (
                            <div key={idx} style={stepWrapperStyle}>
                              <div style={{
                                width: '20px',
                                height: '20px',
                                borderRadius: '50%',
                                backgroundColor: isActive ? 'var(--accent-color)' : 'var(--text-secondary)',
                                display: 'flex',
                                alignItems: 'center',
                                justifyContent: 'center',
                                fontSize: '0.7rem',
                                color: '#ffffff',
                                fontWeight: 700,
                                zIndex: 2,
                                border: '3px solid #1e293b'
                              }}>
                                {idx < stepIndex ? '✓' : idx}
                              </div>
                              <span style={{
                                fontSize: '0.75rem',
                                color: isActive ? 'var(--text-primary)' : 'var(--text-secondary)',
                                marginTop: '0.25rem',
                                fontWeight: isActive ? 600 : 400
                              }}>{step}</span>
                            </div>
                          );
                        })}
                        {/* Connecting Line */}
                        <div style={{
                          position: 'absolute',
                          top: '10px',
                          left: '10%',
                          width: '80%',
                          height: '2px',
                          backgroundColor: 'rgba(255, 255, 255, 0.1)',
                          zIndex: 1
                        }} />
                        <div style={{
                          position: 'absolute',
                          top: '10px',
                          left: '10%',
                          width: `${(stepIndex / 4) * 80}%`,
                          height: '2px',
                          backgroundColor: 'var(--accent-color)',
                          zIndex: 1,
                          transition: 'width 0.5s ease-in-out'
                        }} />
                      </div>
                    ) : (
                      <div style={cancelledBannerStyle}>
                        <XCircle className="w-5 h-5 text-red-500 mr-2" />
                        <span>Workflow terminated. Order has been cancelled due to Payment Failure.</span>
                      </div>
                    )}
                  </div>
                );
              })}
            </div>
          )}
        </section>
      </div>
    </div>
  );
}

// Inline Styles for Visual Excellence (Aesthetic Polish)
const containerStyle: React.CSSProperties = {
  maxWidth: '1200px',
  margin: '0 auto',
  padding: '2.5rem 1.5rem',
  minHeight: '100vh',
  display: 'flex',
  flexDirection: 'column',
  gap: '2rem'
};

const headerStyle: React.CSSProperties = {
  display: 'flex',
  justifyContent: 'space-between',
  alignItems: 'center',
  paddingBottom: '1.5rem',
  borderBottom: '1px solid rgba(255, 255, 255, 0.08)'
};

const brandTitleStyle: React.CSSProperties = {
  fontSize: '2rem',
  fontWeight: 800,
  letterSpacing: '-0.025em',
  color: '#ffffff'
};

const brandDescStyle: React.CSSProperties = {
  color: 'var(--text-secondary)',
  marginTop: '0.25rem',
  fontSize: '0.95rem'
};

const successBadgeStyle: React.CSSProperties = {
  fontSize: '0.8rem',
  fontWeight: 600,
  color: '#22c55e',
  backgroundColor: 'rgba(34, 197, 94, 0.15)',
  padding: '0.35rem 0.65rem',
  borderRadius: '9999px',
  border: '1px solid rgba(34, 197, 94, 0.3)'
};

const errorBadgeStyle: React.CSSProperties = {
  fontSize: '0.8rem',
  fontWeight: 600,
  color: '#ef4444',
  backgroundColor: 'rgba(239, 68, 68, 0.15)',
  padding: '0.35rem 0.65rem',
  borderRadius: '9999px',
  border: '1px solid rgba(239, 68, 68, 0.3)'
};

const iconButtonStyle: React.CSSProperties = {
  backgroundColor: 'rgba(255,255,255,0.05)',
  border: '1px solid rgba(255,255,255,0.1)',
  color: 'var(--text-primary)',
  padding: '0.5rem',
  borderRadius: '0.375rem',
  cursor: 'pointer',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  transition: 'background-color 0.2s'
};

const gridStyle: React.CSSProperties = {
  display: 'grid',
  gridTemplateColumns: 'repeat(auto-fit, minmax(350px, 1fr))',
  gap: '2rem',
  alignItems: 'start'
};

const cardStyle: React.CSSProperties = {
  background: 'var(--card-bg)',
  backdropFilter: 'blur(16px)',
  border: '1px solid var(--card-border)',
  borderRadius: '1rem',
  boxShadow: 'var(--card-shadow)',
  padding: '2rem',
  display: 'flex',
  flexDirection: 'column',
  gap: '1.5rem',
  transition: 'transform 0.2s ease-in-out'
};

const cardHeaderStyle: React.CSSProperties = {
  display: 'flex',
  alignItems: 'center',
  gap: '0.75rem',
  borderBottom: '1px solid rgba(255, 255, 255, 0.05)',
  paddingBottom: '1rem'
};

const cardTitleStyle: React.CSSProperties = {
  fontSize: '1.35rem',
  fontWeight: 700,
  color: '#ffffff'
};

const timeBadgeStyle: React.CSSProperties = {
  marginLeft: 'auto',
  fontSize: '0.75rem',
  color: 'var(--text-secondary)',
  backgroundColor: 'rgba(255, 255, 255, 0.04)',
  padding: '0.25rem 0.5rem',
  borderRadius: '0.25rem'
};

const formStyle: React.CSSProperties = {
  display: 'flex',
  flexDirection: 'column',
  gap: '1.25rem'
};

const inputGroupStyle: React.CSSProperties = {
  display: 'flex',
  flexDirection: 'column',
  gap: '0.5rem'
};

const labelStyle: React.CSSProperties = {
  fontSize: '0.85rem',
  fontWeight: 600,
  color: 'var(--text-primary)',
  display: 'flex',
  alignItems: 'center'
};

const inputStyle: React.CSSProperties = {
  backgroundColor: 'rgba(15, 23, 42, 0.6)',
  border: '1px solid rgba(255, 255, 255, 0.1)',
  borderRadius: '0.5rem',
  padding: '0.75rem 1rem',
  color: '#ffffff',
  fontSize: '0.95rem',
  outline: 'none',
  transition: 'border-color 0.2s'
};

const selectStyle: React.CSSProperties = {
  ...inputStyle,
  cursor: 'pointer'
};

const readOnlyInputStyle: React.CSSProperties = {
  ...inputStyle,
  backgroundColor: 'rgba(15, 23, 42, 0.3)',
  color: 'var(--text-secondary)',
  cursor: 'not-allowed'
};

const submitButtonStyle: React.CSSProperties = {
  backgroundColor: 'var(--accent-color)',
  border: 'none',
  borderRadius: '0.5rem',
  padding: '0.9rem',
  color: '#ffffff',
  fontSize: '1rem',
  fontWeight: 700,
  cursor: 'pointer',
  transition: 'background-color 0.2s, transform 0.1s',
  boxShadow: '0 4px 14px 0 rgba(244, 63, 94, 0.4)'
};

const disabledButtonStyle: React.CSSProperties = {
  ...submitButtonStyle,
  backgroundColor: 'rgba(244, 63, 94, 0.4)',
  cursor: 'not-allowed',
  boxShadow: 'none'
};

const formSuccessMessageStyle: React.CSSProperties = {
  backgroundColor: 'rgba(34, 197, 94, 0.12)',
  border: '1px solid rgba(34, 197, 94, 0.25)',
  color: '#4ade80',
  borderRadius: '0.5rem',
  padding: '0.75rem 1rem',
  fontSize: '0.9rem',
  fontWeight: 500
};

const formErrorMessageStyle: React.CSSProperties = {
  backgroundColor: 'rgba(239, 68, 68, 0.12)',
  border: '1px solid rgba(239, 68, 68, 0.25)',
  color: '#f87171',
  borderRadius: '0.5rem',
  padding: '0.75rem 1rem',
  fontSize: '0.9rem',
  fontWeight: 500
};

const loaderContainerStyle: React.CSSProperties = {
  display: 'flex',
  flexDirection: 'column',
  alignItems: 'center',
  justifyContent: 'center',
  padding: '3rem 0'
};

const emptyContainerStyle: React.CSSProperties = {
  display: 'flex',
  flexDirection: 'column',
  alignItems: 'center',
  justifyContent: 'center',
  padding: '4rem 0',
  textAlign: 'center'
};

const ordersContainerStyle: React.CSSProperties = {
  display: 'flex',
  flexDirection: 'column',
  gap: '1.25rem',
  maxHeight: '600px',
  overflowY: 'auto',
  paddingRight: '0.5rem'
};

const orderRowStyle: React.CSSProperties = {
  backgroundColor: 'rgba(15, 23, 42, 0.4)',
  border: '1px solid rgba(255, 255, 255, 0.05)',
  borderRadius: '0.75rem',
  padding: '1.25rem',
  display: 'flex',
  flexDirection: 'column',
  transition: 'border-color 0.2s'
};

const orderRowHeaderStyle: React.CSSProperties = {
  display: 'flex',
  justifyContent: 'space-between',
  alignItems: 'center'
};

const orderIdBadgeStyle: React.CSSProperties = {
  fontSize: '0.75rem',
  fontWeight: 700,
  color: 'var(--text-secondary)',
  backgroundColor: 'rgba(255, 255, 255, 0.06)',
  padding: '0.2rem 0.4rem',
  borderRadius: '0.25rem'
};

const progressTrackerStyle: React.CSSProperties = {
  display: 'flex',
  justifyContent: 'space-between',
  alignItems: 'center',
  position: 'relative',
  paddingTop: '0.5rem',
  marginTop: '0.5rem'
};

const stepWrapperStyle: React.CSSProperties = {
  display: 'flex',
  flexDirection: 'column',
  alignItems: 'center',
  flex: 1,
  textAlign: 'center'
};

const cancelledBannerStyle: React.CSSProperties = {
  display: 'flex',
  alignItems: 'center',
  backgroundColor: 'rgba(239, 68, 68, 0.08)',
  border: '1px solid rgba(239, 68, 68, 0.2)',
  borderRadius: '0.375rem',
  padding: '0.65rem 0.75rem',
  color: '#f87171',
  fontSize: '0.85rem',
  marginTop: '0.5rem',
  fontWeight: 500
};

export default App;
