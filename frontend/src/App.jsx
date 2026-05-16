import { useEffect, useMemo, useState } from 'react';
import {
  ArrowRight,
  BarChart3,
  Code2,
  Globe2,
  Handshake,
  Instagram,
  Layers3,
  LockKeyhole,
  Menu,
  MessageCircle,
  MoonStar,
  Send,
  ShieldCheck,
  Sparkles,
  Sun,
  Twitter,
  X,
  Youtube,
} from 'lucide-react';
import companyBanner from './assets/fsg-banner.png';
import companyLogo from './assets/fsg-logo-circle.png';

const API_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8081';

const services = [
  {
    icon: Code2,
    title: 'Application Development',
    text: 'Web apps, dashboards, portals, and business tools built for scale.',
  },
  {
    icon: Sparkles,
    title: 'Brand Transformation',
    text: 'Identity, content systems, campaign pages, and customer-facing design.',
  },
  {
    icon: BarChart3,
    title: 'Growth Analytics',
    text: 'Data-backed decisions, reporting layers, and optimization loops.',
  },
  {
    icon: ShieldCheck,
    title: 'Secure Digital Foundation',
    text: 'Authentication-ready systems, backend APIs, and database structure.',
  },
];

const process = [
  { label: 'Fix', text: 'Audit bottlenecks, brand gaps, and growth blockers.' },
  { label: 'Set', text: 'Build a scalable technology and marketing foundation.' },
  { label: 'Go', text: 'Launch, measure, optimize, and grow with confidence.' },
];

const socialLinks = [
  { label: 'Instagram', href: 'https://instagram.com/your_instagram_id', icon: Instagram },
  { label: 'Telegram', href: 'https://t.me/your_telegram_username', icon: Send },
  { label: 'YouTube', href: 'https://youtube.com/@your_channel', icon: Youtube },
  { label: 'Twitter', href: 'https://x.com/your_x_handle', icon: Twitter },
  { label: 'WhatsApp', href: 'https://wa.me/911234567890', icon: MessageCircle },
];

function App() {
  const [menuOpen, setMenuOpen] = useState(false);
  const [theme, setTheme] = useState(() => localStorage.getItem('fix-set-go-theme') ?? 'dark');
  const [authMode, setAuthMode] = useState('login');
  const [authOpen, setAuthOpen] = useState(false);
  const [authStatus, setAuthStatus] = useState('');
  const [leadStatus, setLeadStatus] = useState('');
  const [auth, setAuth] = useState(() => {
    const stored = localStorage.getItem('fix-set-go-auth');
    return stored ? JSON.parse(stored) : null;
  });

  useEffect(() => {
    localStorage.setItem('fix-set-go-theme', theme);
  }, [theme]);

  const navItems = useMemo(
    () => [
      ['Services', 'services'],
      ['Process', 'process'],
      ['Why Us', 'why-us'],
      ['Contact', 'contact'],
    ],
    [],
  );

  function saveAuth(data) {
    localStorage.setItem('fix-set-go-auth', JSON.stringify(data));
    setAuth(data);
    setAuthOpen(false);
    setAuthStatus('');
  }

  function logout() {
    localStorage.removeItem('fix-set-go-auth');
    setAuth(null);
  }

  async function submitAuth(event) {
    event.preventDefault();
    setAuthStatus('Please wait...');
    const form = new FormData(event.currentTarget);
    const payload =
      authMode === 'register'
        ? {
            fullName: form.get('fullName'),
            email: form.get('email'),
            password: form.get('password'),
            company: form.get('company'),
          }
        : {
            email: form.get('email'),
            password: form.get('password'),
          };

    try {
      const response = await fetch(`${API_URL}/api/auth/${authMode}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      });

      if (!response.ok) {
        throw new Error('Check your details and try again.');
      }

      saveAuth(await response.json());
    } catch (error) {
      setAuthStatus(error.message);
    }
  }

  async function submitLead(event) {
    event.preventDefault();
    setLeadStatus('Sending...');
    const formElement = event.currentTarget;
    const form = new FormData(formElement);
    const payload = {
      name: form.get('name'),
      email: form.get('email'),
      company: form.get('company'),
      service: form.get('service'),
      message: form.get('message'),
    };

    try {
      const response = await fetch(`${API_URL}/api/leads`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      });

      if (!response.ok) {
        throw new Error('Something went wrong. Please try again.');
      }

      formElement.reset();
      setLeadStatus('Thanks for your message. Please wait while our admin reviews your request and contacts you by email.');
    } catch (error) {
      setLeadStatus(error.message);
    }
  }

  return (
    <div className={`site-shell theme-${theme}`}>
      <header className="topbar">
        <a className="brand" href="#home" aria-label="Fix Set Go home">
          <img className="brand-logo" src={companyLogo} alt="Fix Set Go logo" />
          <span>Fix Set Go</span>
        </a>

        <nav className={menuOpen ? 'nav nav-open' : 'nav'} aria-label="Main navigation">
          {navItems.map(([label, id]) => (
            <a key={id} href={`#${id}`} onClick={() => setMenuOpen(false)}>
              {label}
            </a>
          ))}
        </nav>

        <div className="account-actions">
          <button
            className="ghost-button theme-toggle"
            type="button"
            aria-label={`Switch to ${theme === 'dark' ? 'light' : 'dark'} mode`}
            onClick={() => setTheme(theme === 'dark' ? 'light' : 'dark')}
          >
            {theme === 'dark' ? <Sun size={16} /> : <MoonStar size={16} />}
            <span>{theme === 'dark' ? 'Light Mode' : 'Dark Mode'}</span>
          </button>
          {auth ? (
            <>
              <span className="welcome">Hi, {auth.user.fullName.split(' ')[0]}</span>
              <button className="ghost-button" type="button" onClick={logout}>
                Logout
              </button>
            </>
          ) : (
            <button className="ghost-button" type="button" onClick={() => setAuthOpen(true)}>
              <LockKeyhole size={16} />
              Client Login
            </button>
          )}
          <button className="icon-button menu-button" type="button" onClick={() => setMenuOpen(!menuOpen)}>
            {menuOpen ? <X size={20} /> : <Menu size={20} />}
          </button>
        </div>
      </header>

      <main>
        <section className="hero" id="home">
          <div className="hero-content">
            <p className="eyebrow">Digital solutions for B2B and B2C growth</p>
            <h1>Fix Set Go</h1>
            <p className="hero-copy">
              We bridge imagination and execution through integrated technology, creative design, and launch-ready
              digital systems.
            </p>
            <div className="hero-actions">
              <a className="primary-button" href="#contact">
                Start a Project
                <ArrowRight size={18} />
              </a>
              <a className="secondary-button" href="#services">
                Explore Services
              </a>
            </div>
          </div>
          <div className="hero-visual" aria-label="Technology and growth visual">
            <img className="hero-banner-image" src={companyBanner} alt="Fix Set Go company banner" />
            <div className="hero-badge">
              <img src={companyLogo} alt="" aria-hidden="true" />
              <div>
                <strong>All-In-One Solution Partner</strong>
                <span>Technology, branding, and growth execution</span>
              </div>
            </div>
          </div>
        </section>

        <section className="section" id="services">
          <div className="section-heading">
            <p className="eyebrow">What we do</p>
            <h2>We fix bottlenecks, set the foundation, and go to market with impact.</h2>
          </div>
          <div className="service-grid">
            {services.map((service) => {
              const Icon = service.icon;
              return (
                <article className="service-card" key={service.title}>
                  <Icon size={34} />
                  <h3>{service.title}</h3>
                  <p>{service.text}</p>
                </article>
              );
            })}
          </div>
        </section>

        <section className="process-band" id="process">
          <div className="section-heading">
            <p className="eyebrow">Our process</p>
            <h2>Simple enough to move fast. Strong enough to scale.</h2>
          </div>
          <div className="process-grid">
            {process.map((step, index) => (
              <article className="process-step" key={step.label}>
                <span>{String(index + 1).padStart(2, '0')}</span>
                <h3>{step.label}</h3>
                <p>{step.text}</p>
              </article>
            ))}
          </div>
        </section>

        <section className="why-section" id="why-us">
          <div className="why-copy">
            <p className="eyebrow">Why choose us</p>
            <h2>Creativity meets tech, without the usual project chaos.</h2>
            <p>
              Our team blends strategic branding, application engineering, analytics, and end-to-end execution so your
              business can focus on scale.
            </p>
          </div>
          <div className="why-list">
            <div>
              <Globe2 size={26} />
              <strong>Built for B2B and B2C</strong>
              <span>Corporate partnerships, customer funnels, and digital products.</span>
            </div>
            <div>
              <Layers3 size={26} />
              <strong>End-to-end delivery</strong>
              <span>Concept, design, backend, frontend, launch, and optimization.</span>
            </div>
            <div>
              <Handshake size={26} />
              <strong>Partner mindset</strong>
              <span>Clear communication and practical execution from day one.</span>
            </div>
          </div>
        </section>

        <section className="contact-section" id="contact">
          <div className="contact-copy">
            <p className="eyebrow">Partner with us</p>
            <h2>Tell us what you want to fix, set, and launch.</h2>
            <p>
              Share a short brief and the backend will store your lead in the database. Registered users can also log in
              for protected client access.
            </p>
          </div>

          <form className="contact-form" onSubmit={submitLead}>
            <label>
              Name
              <input name="name" required placeholder="Your name" />
            </label>
            <label>
              Email
              <input name="email" type="email" required placeholder="you@company.com" />
            </label>
            <label>
              Company
              <input name="company" placeholder="Company name" />
            </label>
            <label>
              Service
              <select name="service" required defaultValue="">
                <option value="" disabled>
                  Choose service
                </option>
                <option>Application Development</option>
                <option>Brand Transformation</option>
                <option>Growth Analytics</option>
                <option>Secure Digital Foundation</option>
              </select>
            </label>
            <label className="full-field">
              Message
              <textarea name="message" required rows="5" placeholder="What are you building?" />
            </label>
            <button className="primary-button full-field" type="submit">
              Send Brief
              <ArrowRight size={18} />
            </button>
            {leadStatus && <p className="form-status full-field">{leadStatus}</p>}
          </form>
        </section>
      </main>

      <footer>
        <div className="footer-left">
          <strong>Fix Set Go</strong>
          <span>Fix your challenges. Set your strategy. Go achieve your vision.</span>
        </div>
        <nav className="footer-social" aria-label="Social links">
          {socialLinks.map((item) => {
            const Icon = item.icon;
            return (
              <a
                key={item.label}
                className="social-link"
                href={item.href}
                target="_blank"
                rel="noreferrer"
                aria-label={item.label}
                title={item.label}
              >
                <Icon size={18} />
              </a>
            );
          })}
        </nav>
      </footer>

      {authOpen && (
        <div className="modal-backdrop" role="dialog" aria-modal="true" aria-label="Client authentication">
          <div className="auth-modal">
            <button className="icon-button close-button" type="button" onClick={() => setAuthOpen(false)}>
              <X size={20} />
            </button>
            <p className="eyebrow">Client access</p>
            <h2>{authMode === 'login' ? 'Login to your account' : 'Create your account'}</h2>

            <div className="auth-tabs">
              <button
                className={authMode === 'login' ? 'active' : ''}
                type="button"
                onClick={() => setAuthMode('login')}
              >
                Login
              </button>
              <button
                className={authMode === 'register' ? 'active' : ''}
                type="button"
                onClick={() => setAuthMode('register')}
              >
                Register
              </button>
            </div>

            <form className="auth-form" onSubmit={submitAuth}>
              {authMode === 'register' && (
                <>
                  <label>
                    Full name
                    <input name="fullName" required placeholder="Your full name" />
                  </label>
                  <label>
                    Company
                    <input name="company" placeholder="Company name" />
                  </label>
                </>
              )}
              <label>
                Email
                <input name="email" type="email" required placeholder="you@company.com" />
              </label>
              <label>
                Password
                <input name="password" type="password" minLength="8" required placeholder="Minimum 8 characters" />
              </label>
              <button className="primary-button" type="submit">
                {authMode === 'login' ? 'Login' : 'Create Account'}
                <ArrowRight size={18} />
              </button>
              {authStatus && <p className="form-status">{authStatus}</p>}
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default App;
