const C='sufre-v6';
const IMGS=["a0-0.jpg", "a0-1.jpg", "a1-0.jpg", "a1-1.jpg", "a2-0.jpg", "a2-1.jpg", "a3-0.jpg", "a3-1.jpg", "a4-0.jpg", "a4-1.jpg", "a5-0.jpg", "a5-1.jpg", "a6-0.jpg", "a6-1.jpg", "a7-0.jpg", "a7-1.jpg", "b1-0.jpg", "b1-1.jpg", "b2-0.jpg", "b2-1.jpg", "b3-0.jpg", "b3-1.jpg", "b4-0.jpg", "b4-1.jpg", "b5-0.jpg", "b5-1.jpg", "b6-0.jpg", "b6-1.jpg", "b7-0.jpg", "b7-1.jpg", "el-0.jpg", "el-1.jpg", "o1-0.jpg", "o1-1.jpg", "o2-0.jpg", "o2-1.jpg", "o3-0.jpg", "o3-1.jpg", "o4-0.jpg", "o4-1.jpg", "o5-0.jpg", "o5-1.jpg", "tr-0.jpg", "tr-1.jpg", "v1-0.jpg", "v1-1.jpg", "v10-0.jpg", "v10-1.jpg", "v11-0.jpg", "v11-1.jpg", "v12-0.jpg", "v12-1.jpg", "v2-0.jpg", "v2-1.jpg", "v3-0.jpg", "v3-1.jpg", "v4-0.jpg", "v4-1.jpg", "v5-0.jpg", "v5-1.jpg", "v6-0.jpg", "v6-1.jpg", "v7-0.jpg", "v7-1.jpg", "v8-0.jpg", "v8-1.jpg", "v9-0.jpg", "v9-1.jpg"].map(f=>'mesq/'+f);
const CORE=['./','index.html','manifest.webmanifest','icon-192.png','icon-512.png','apple-touch-icon.png'];
self.addEventListener('install',e=>{e.waitUntil(caches.open(C).then(c=>c.addAll(CORE)).then(()=>self.skipWaiting()));});
self.addEventListener('activate',e=>{e.waitUntil(caches.keys().then(ks=>Promise.all(ks.filter(k=>k!==C).map(k=>caches.delete(k)))).then(()=>self.clients.claim()));
  // workout photos: cached in the background so the gym section works offline
  caches.open(C).then(c=>c.addAll(IMGS)).catch(()=>{});});
self.addEventListener('fetch',e=>{
  const r=e.request; if(r.method!=='GET') return;
  const u=new URL(r.url);
  if(u.origin===location.origin && (r.mode==='navigate'||u.pathname.endsWith('/')||u.pathname.endsWith('index.html'))){
    e.respondWith(fetch(r).then(res=>{const cp=res.clone(); caches.open(C).then(c=>c.put(r,cp)); return res;}).catch(()=>caches.match(r).then(m=>m||caches.match('index.html'))));
    return;
  }
  e.respondWith(caches.match(r).then(m=>m||fetch(r).then(res=>{ if(res.ok&&(u.origin===location.origin||u.host.includes('fonts.'))){const cp=res.clone(); caches.open(C).then(c=>c.put(r,cp));} return res;})));
});
